package com.travelplanner.app;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler cho upload ảnh - nhận Base64 hoặc multipart form-data
 */
public class UploadHandler implements HttpHandler {
    private static final String UPLOAD_DIR = "src/main/resource/public/uploads";

    @Override
    public void handle(HttpExchange t) throws IOException {
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(t.getRequestMethod())) {
            t.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equals(t.getRequestMethod())) {
            sendJson(t, 405, "{\"error\": \"Method not allowed\"}");
            return;
        }

        try {
            String contentType = t.getRequestHeaders().getFirst("Content-Type");
            if (contentType == null) contentType = "";
            String response;

            if (contentType.contains("application/json")) {
                response = handleBase64Upload(t);
            } else if (contentType.contains("multipart/form-data")) {
                response = handleMultipartUpload(t);
            } else {
                sendJson(t, 400, "{\"error\": \"Content-Type must be application/json or multipart/form-data\"}");
                return;
            }

            sendJson(t, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(t, 500, "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String handleBase64Upload(HttpExchange t) throws IOException {
        String body = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String base64 = extractBase64FromJson(body);
        String filename = extractFilenameFromJson(body);

        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Missing 'image' field in JSON (base64 string)");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64.replaceAll("^data:image/[^;]+;base64,", ""));
        String savedUrl = saveFile(imageBytes, filename != null ? filename : "upload.jpg");
        return "{\"url\": \"" + escapeJson(savedUrl) + "\"}";
    }

    private String extractBase64FromJson(String json) {
        Pattern p = Pattern.compile("\"image\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String extractFilenameFromJson(String json) {
        Pattern p = Pattern.compile("\"filename\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String handleMultipartUpload(HttpExchange t) throws IOException {
        String contentType = t.getRequestHeaders().getFirst("Content-Type");
        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new IllegalArgumentException("Invalid multipart boundary");
        }

        byte[] body = t.getRequestBody().readAllBytes();
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);

        int start = findBoundary(body, boundaryBytes, 0);
        if (start < 0) {
            throw new IllegalArgumentException("No multipart boundary found");
        }

        int headerEnd = findBytes(body, "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1), start);
        if (headerEnd < 0) headerEnd = findBytes(body, "\n\n".getBytes(StandardCharsets.ISO_8859_1), start);

        String headers = new String(body, start, headerEnd - start, StandardCharsets.ISO_8859_1);
        String filename = extractFilenameFromHeader(headers);
        int contentStart = headerEnd + 4;
        int contentEnd = findBoundary(body, boundaryBytes, contentStart);
        if (contentEnd < 0) contentEnd = body.length;
        else contentEnd -= 2;

        byte[] fileContent = new byte[contentEnd - contentStart];
        System.arraycopy(body, contentStart, fileContent, 0, fileContent.length);

        String savedUrl = saveFile(fileContent, filename != null ? filename : "upload.jpg");
        return "{\"url\": \"" + escapeJson(savedUrl) + "\"}";
    }

    private String extractBoundary(String contentType) {
        if (contentType == null) return null;
        int idx = contentType.indexOf("boundary=");
        if (idx < 0) return null;
        String b = contentType.substring(idx + 9).trim().replaceAll("\"", "");
        int end = b.indexOf(';');
        return end > 0 ? b.substring(0, end).trim() : b.trim();
    }

    private int findBoundary(byte[] body, byte[] boundary, int from) {
        for (int i = from; i <= body.length - boundary.length; i++) {
            boolean match = true;
            for (int j = 0; j < boundary.length; j++) {
                if (body[i + j] != boundary[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    private int findBytes(byte[] body, byte[] search, int from) {
        for (int i = from; i <= body.length - search.length; i++) {
            boolean match = true;
            for (int j = 0; j < search.length; j++) {
                if (body[i + j] != search[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    private String extractFilenameFromHeader(String headers) {
        Pattern p = Pattern.compile("filename=\"?([^\";\\r\\n]+)\"?");
        Matcher m = p.matcher(headers);
        return m.find() ? m.group(1).trim() : null;
    }

    private String saveFile(byte[] content, String originalFilename) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);

        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot > 0) ext = originalFilename.substring(dot);
        String filename = "img_" + System.currentTimeMillis() + ext;
        Path filePath = uploadPath.resolve(filename);

        Files.write(filePath, content);
        return "/uploads/" + filename;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void sendJson(HttpExchange t, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(status, bytes.length);
        t.getResponseBody().write(bytes);
        t.close();
    }
}
