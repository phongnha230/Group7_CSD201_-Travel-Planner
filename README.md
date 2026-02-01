# Travel Planner (Group 7)

A Java-based Travel Planner application designed to demonstrate the implementation of fundamental data structures. This project currently features a tour itinerary management system using a custom Linked List and lays the groundwork for future implementations using Binary Search Trees (BST) and Graphs.

**Pair Programming:** Nhã and Linh

## Features

- **Tour Management:**
  - Add new tour locations to the itinerary.
  - Remove locations from the itinerary.
  - View the current tour plan (from Start to End).
- **Custom Data Structures:**
  - `MyLinkedList`: A custom implementation of a singly linked list with a tail pointer for O(1) insertion at the end.
- **Data Models:**
  - `TourLocation`: Represents a destination with ID, name, description, and price.
  - `Customer`: Represents a customer (structure defined, logic pending).

## Project Structure

The source code is organized into the following packages under `src/main/java/com/travelplanner`:

- **`app`**: Contains the application entry points.
  - `ConsoleApp.java`: The current main entry point for testing the Linked List functionality.
  - `WebServer.java`: Placeholder for future web server implementation.
- **`entities`**: Contains the data models.
  - `TourLocation.java`
  - `Customer.java`
- **`structures`**: Contains custom data structure implementations.
  - `MyLinkedList.java`
  - `MyBST.java` (Planned)
  - `MyGraph.java` (Planned)
  - `Node.java`: Generic node class for the data structures.

## Prerequisites

- **Java Development Kit (JDK):** Version 21 or higher.
- **Maven:** For dependency management and building the project.

## How to Build and Run

### Build the Project

Use Maven to compile the project:

```bash
mvn compile
```

### Run the Application

Currently, the main logic is located in `ConsoleApp`. You can run it using the following Maven command:

```bash
mvn exec:java -Dexec.mainClass="com.travelplanner.app.ConsoleApp"
```

This will execute the `ConsoleApp` which demonstrates adding locations to a tour and removing them.

## Future Work

- **Graph Implementation (`MyGraph`):** To manage routes and distances between locations.
- **Binary Search Tree (`MyBST`):** To allow efficient searching of locations or customers.
- **Web Interface:** Integrating a web server to expose the functionality via an API or UI.
