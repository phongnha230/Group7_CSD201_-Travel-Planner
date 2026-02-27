package com.travelplanner.app;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.travelplanner.entities.TourLocation;
import com.travelplanner.entities.Customer;
import com.travelplanner.structures.MyLinkedList;
import com.travelplanner.structures.MyBST;
import com.travelplanner.structures.MyGraph;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// Handler cho /api/tour (Linked List operations)
class TourHandler implements HttpHandler {
    private final MyLinkedList tourList;
    private final MyGraph graph;

    public TourHandler(MyLinkedList tourList, MyGraph graph) {
        this.tourList = tourList;
        this.graph = graph;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String method = t.getRequestMethod();
        String response = "";
        int statusCode = 200;

        try {
            switch (method) {
            case "GET": {
                // Get all tour locations
                Object[] locations = tourList.toArray();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < locations.length; i++) {
                    TourLocation loc = (TourLocation) locations[i];
                    String imageUrl = loc.getImageUrl();
                    String imageJson = imageUrl != null ? ", \"imageUrl\":\"" + escapeJson(imageUrl) + "\"" : "";
                    json.append(
                            String.format("{\"id\":\"%s\", \"name\":\"%s\", \"description\":\"%s\", \"price\":%.2f%s}",
                                    escapeJson(loc.getId()), escapeJson(loc.getName()), escapeJson(loc.getDescription()),
                                    loc.getPrice(), imageJson));
                    if (i < locations.length - 1)
                        json.append(",");
                }
                json.append("]");
                response = json.toString();

                break;
            }
            case "POST": {
                // Add location to tour (với giá cả và ảnh tùy chọn)
                URI uri = t.getRequestURI();
                Map<String, String> params = queryToMap(uri.getQuery());
                String id = params.get("id");
                String priceStr = params.get("price");
                String imageUrl = params.get("imageUrl");
                String position = params.get("position");
                String indexStr = params.get("index");

                if (id != null) {
                    List<TourLocation> allLocs = graph.getAllLocations();
                    TourLocation found = null;
                    for (TourLocation loc : allLocs) {
                        if (loc.getId().equals(id)) {
                            found = loc;
                            break;
                        }
                    }
                    if (found != null) {
                        double price = 0;
                        if (priceStr != null && !priceStr.isEmpty()) {
                            try {
                                price = Double.parseDouble(priceStr);
                            } catch (NumberFormatException ignored) {}
                        }
                        TourLocation tourCopy = new TourLocation(
                                found.getId(), found.getName(), found.getDescription(),
                                price, found.getX(), found.getY());
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            tourCopy.setImageUrl(imageUrl);
                        }
                        boolean added = false;
                        if ("head".equalsIgnoreCase(position)) {
                            tourList.addAtHead(tourCopy);
                            added = true;
                        } else if ("index".equalsIgnoreCase(position) && indexStr != null && !indexStr.isEmpty()) {
                            try {
                                int idx = Integer.parseInt(indexStr);
                                added = tourList.addAtIndex(idx, tourCopy);
                            } catch (NumberFormatException ignored) {}
                        }
                        if (!added) {
                            tourList.addAtTail(tourCopy);
                        }
                        response = "{\"success\": true, \"message\": \"Added to tour\"}";
                    } else {
                        response = "{\"error\": \"Location not found\"}";
                        statusCode = 404;
                    }
                } else {
                    response = "{\"error\": \"Missing id parameter\"}";
                    statusCode = 400;
                }

                break;
            }
            case "DELETE": {
                // Remove location from tour
                URI uri = t.getRequestURI();
                Map<String, String> params = queryToMap(uri.getQuery());
                String id = params.get("id");

                if (id != null) {
                    boolean removed = tourList.removeLocation(id);
                    if (removed) {
                        response = "{\"success\": true, \"message\": \"Removed from tour\"}";
                    } else {
                        response = "{\"error\": \"Location not found in tour\"}";
                        statusCode = 404;
                    }
                } else {
                    response = "{\"error\": \"Missing id parameter\"}";
                    statusCode = 400;
                }
                break;
            }
            default:
                response = "{\"error\": \"Method not allowed\"}";
                statusCode = 405;
                break;
            }

        } catch (RuntimeException e) {
            System.err.println("TourHandler error: " + e.getMessage());
            response = "{\"error\": \"" + e.getMessage() + "\"}";
            statusCode = 500;
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null)
            return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length >= 2) {
                try {
                    result.put(pair[0], java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                } catch (IllegalArgumentException e) {
                    result.put(pair[0], pair[1]);
                }
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}

// Handler cho /api/customers (BST operations)
class CustomerHandler implements HttpHandler {
    private final MyBST customerTree;

    public CustomerHandler(MyBST customerTree) {
        this.customerTree = customerTree;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String method = t.getRequestMethod();
        String response = "";
        int statusCode = 200;

        try {
            switch (method) {
            case "GET": {
                URI uri = t.getRequestURI();
                Map<String, String> params = queryToMap(uri.getQuery());
                String searchId = params.get("id");

                if (searchId != null) {
                    // Search for specific customer
                    Customer found = customerTree.search(searchId);
                    if (found != null) {
                        response = String.format("{\"id\":\"%s\", \"name\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\"}",
                                found.getId(), found.getName(), found.getPhone(), found.getEmail());
                    } else {
                        response = "{\"error\": \"Customer not found\"}";
                        statusCode = 404;
                    }
                } else {
                    // Return all customers and tree structure for UI
                    int count = customerTree.count();
                    StringBuilder sb = new StringBuilder();
                    sb.append("{\"count\":").append(count).append(",\"customers\":[");
                    var list = customerTree.getAllInOrder();
                    for (int i = 0; i < list.size(); i++) {
                        Customer c = list.get(i);
                        if (i > 0) sb.append(",");
                        sb.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\"}",
                                escapeJson(c.getId()), escapeJson(c.getName()), escapeJson(c.getPhone()), escapeJson(c.getEmail())));
                    }
                    sb.append("],\"tree\":[");
                    var tree = customerTree.getTreeStructure();
                    for (int i = 0; i < tree.size(); i++) {
                        var m = tree.get(i);
                        if (i > 0) sb.append(",");
                        sb.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"left\":%s,\"right\":%s}",
                                escapeJson(m.get("id")), escapeJson(m.get("name")),
                                m.get("left") == null ? "null" : "\"" + escapeJson(m.get("left")) + "\"",
                                m.get("right") == null ? "null" : "\"" + escapeJson(m.get("right")) + "\""));
                    }
                    sb.append("]}");
                    response = sb.toString();
                }

                break;
            }
            case "POST": {
                // Add customer
                URI uri = t.getRequestURI();
                Map<String, String> params = queryToMap(uri.getQuery());
                String id = params.get("id");
                String name = params.get("name");
                String phone = params.get("phone");
                String email = params.get("email");

                if (id != null && name != null && phone != null && email != null) {
                    if (customerTree.search(id) != null) {
                        response = "{\"success\": false, \"error\": \"Customer ID already exists. Please use a different ID.\"}";
                        statusCode = 400;
                    } else {
                        Customer newCustomer = new Customer(id, name, phone, email);
                        customerTree.insert(newCustomer);
                        response = "{\"success\": true, \"message\": \"Customer added\"}";
                    }
                } else {
                    response = "{\"error\": \"Missing required parameters (id, name, phone, email)\"}";
                    statusCode = 400;
                }

                break;
            }
            case "DELETE": {
                // Delete customer
                URI uri = t.getRequestURI();
                Map<String, String> params = queryToMap(uri.getQuery());
                String id = params.get("id");

                if (id != null) {
                    customerTree.delete(id);
                    response = "{\"success\": true, \"message\": \"Customer deleted\"}";
                } else {
                    response = "{\"error\": \"Missing id parameter\"}";
                    statusCode = 400;
                }
                break;
            }
            default:
                response = "{\"error\": \"Method not allowed\"}";
                statusCode = 405;
                break;
            }

        } catch (RuntimeException e) {
            System.err.println("CustomerHandler error: " + e.getMessage());
            response = "{\"error\": \"" + e.getMessage() + "\"}";
            statusCode = 500;
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null)
            return result;
        try {
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length > 1) {
                    result.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                } else {
                    result.put(pair[0], "");
                }
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return result;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
