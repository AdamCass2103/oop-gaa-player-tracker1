package com.gaa.player.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;
    private Scanner scanner;

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            displayMenu();
            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    displayAllPlayers();
                    break;
                case 2:
                    displayPlayerById();
                    break;
                case 3:
                    addNewPlayer();
                    break;
                case 4:
                    deletePlayer();
                    break;
                case 5:
                    filterPlayers();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nGAA Player Tracker Menu");
        System.out.println("1. Display All Players");
        System.out.println("2. Display Player by ID");
        System.out.println("3. Add New Player");
        System.out.println("4. Delete Player");
        System.out.println("5. Filter Players");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getMenuChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private void displayAllPlayers() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject request = new JSONObject();
            request.put("command", "getAllPlayers");
            out.println(request.toString());

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                JSONArray players = jsonResponse.getJSONArray("data");
                System.out.println("\nAll GAA Players:");
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.printf("| %-3s | %-20s | %-3s | %-6s | %-6s | %-15s | %-10s | %-15s | %-4s | %-4s | %-6s |%n",
                        "ID", "Name", "Age", "Height", "Weight", "Position", "County", "Club", "Gls", "Pts", "Active");
                System.out.println("--------------------------------------------------------------------------------------------------");

                for (int i = 0; i < players.length(); i++) {
                    JSONObject player = players.getJSONObject(i);
                    System.out.printf("| %-3d | %-20s | %-3d | %-6.2f | %-6.2f | %-15s | %-10s | %-15s | %-4d | %-4d | %-6s |%n",
                            player.getInt("id"),
                            player.getString("name"),
                            player.getInt("age"),
                            player.getDouble("height"),
                            player.getDouble("weight"),
                            player.getString("position"),
                            player.getString("county"),
                            player.getString("club"),
                            player.getInt("goalsScored"),
                            player.getInt("pointsScored"),
                            player.getBoolean("isActive") ? "Yes" : "No");
                }
                System.out.println("--------------------------------------------------------------------------------------------------");
            } else {
                System.out.println("Error retrieving players.");
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    private void displayPlayerById() {
        System.out.print("Enter Player ID: ");
        int id = scanner.nextInt();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject request = new JSONObject();
            request.put("command", "getPlayerById");
            request.put("id", id);
            out.println(request.toString());

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                JSONObject player = jsonResponse.getJSONObject("data");
                System.out.println("\nPlayer Details:");
                System.out.println("----------------------------------------");
                System.out.println("ID: " + player.getInt("id"));
                System.out.println("Name: " + player.getString("name"));
                System.out.println("Age: " + player.getInt("age"));
                System.out.println("Height: " + player.getDouble("height") + "m");
                System.out.println("Weight: " + player.getDouble("weight") + "kg");
                System.out.println("Position: " + player.getString("position"));
                System.out.println("County: " + player.getString("county"));
                System.out.println("Club: " + player.getString("club"));
                System.out.println("Goals Scored: " + player.getInt("goalsScored"));
                System.out.println("Points Scored: " + player.getInt("pointsScored"));
                System.out.println("Active: " + (player.getBoolean("isActive") ? "Yes" : "No"));
                System.out.println("----------------------------------------");
            } else {
                System.out.println("Player not found with ID: " + id);
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    private void addNewPlayer() {
        scanner.nextLine(); // Consume newline

        System.out.println("\nAdd New Player");
        System.out.println("----------------");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = scanner.nextInt();

        System.out.print("Height (m): ");
        float height = scanner.nextFloat();

        System.out.print("Weight (kg): ");
        float weight = scanner.nextFloat();

        scanner.nextLine(); // Consume newline
        System.out.print("Position: ");
        String position = scanner.nextLine();

        System.out.print("County: ");
        String county = scanner.nextLine();

        System.out.print("Club: ");
        String club = scanner.nextLine();

        System.out.print("Goals Scored: ");
        int goalsScored = scanner.nextInt();

        System.out.print("Points Scored: ");
        int pointsScored = scanner.nextInt();

        System.out.print("Is Active (true/false): ");
        boolean isActive = scanner.nextBoolean();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject playerData = new JSONObject();
            playerData.put("name", name);
            playerData.put("age", age);
            playerData.put("height", height);
            playerData.put("weight", weight);
            playerData.put("position", position);
            playerData.put("county", county);
            playerData.put("club", club);
            playerData.put("goalsScored", goalsScored);
            playerData.put("pointsScored", pointsScored);
            playerData.put("isActive", isActive);

            JSONObject request = new JSONObject();
            request.put("command", "insertPlayer");
            request.put("player", playerData);
            out.println(request.toString());

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                JSONObject insertedPlayer = jsonResponse.getJSONObject("data");
                System.out.println("\nPlayer added successfully with ID: " + insertedPlayer.getInt("id"));
            } else {
                System.out.println("Failed to add player.");
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    private void deletePlayer() {
        System.out.print("Enter Player ID to delete: ");
        int id = scanner.nextInt();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            //                                                              ^ Added missing parenthesis

            JSONObject request = new JSONObject();
            request.put("command", "deletePlayer");
            request.put("id", id);
            out.println(request.toString());

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                System.out.println("Player deleted successfully.");
            } else {
                System.out.println("Failed to delete player.");
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    private void filterPlayers() {
        scanner.nextLine(); // Consume newline
        System.out.print("Enter filter (county, club, or position): ");
        String filter = scanner.nextLine();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject request = new JSONObject();
            request.put("command", "filterPlayers");
            request.put("filter", filter);
            out.println(request.toString());

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("success")) {
                JSONArray players = jsonResponse.getJSONArray("data");
                System.out.println("\nFiltered Players (" + filter + "):");
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.printf("| %-3s | %-20s | %-3s | %-6s | %-6s | %-15s | %-10s | %-15s | %-4s | %-4s | %-6s |%n",
                        "ID", "Name", "Age", "Height", "Weight", "Position", "County", "Club", "Gls", "Pts", "Active");
                System.out.println("--------------------------------------------------------------------------------------------------");

                for (int i = 0; i < players.length(); i++) {
                    JSONObject player = players.getJSONObject(i);
                    System.out.printf("| %-3d | %-20s | %-3d | %-6.2f | %-6.2f | %-15s | %-10s | %-15s | %-4d | %-4d | %-6s |%n",
                            player.getInt("id"),
                            player.getString("name"),
                            player.getInt("age"),
                            player.getDouble("height"),
                            player.getDouble("weight"),
                            player.getString("position"),
                            player.getString("county"),
                            player.getString("club"),
                            player.getInt("goalsScored"),
                            player.getInt("pointsScored"),
                            player.getBoolean("isActive") ? "Yes" : "No");
                }
                System.out.println("--------------------------------------------------------------------------------------------------");
            } else {
                System.out.println("No players found matching filter: " + filter);
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}