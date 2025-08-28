package com.gaa.player.server;

import com.gaa.player.dao.MockPlayerDAO;
import com.gaa.player.dao.IPlayerDAO;
import com.gaa.player.dao.PlayerDAO;
import com.gaa.player.util.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8888;
    private static final int THREAD_POOL_SIZE = 10;
    private final IPlayerDAO playerDAO;

    public Server() {
        // Try to use real database first, fall back to mock data if connection fails
        this.playerDAO = createPlayerDAO();
    }

    private IPlayerDAO createPlayerDAO() {
        System.out.println(" Attempting to connect to database...");

        try {
            // Test database connection
            Connection testConnection = DBConnection.getInstance().getConnection();
            if (testConnection != null && !testConnection.isClosed()) {
                System.out.println(" Database connection successful! Using PlayerDAO");
                return new PlayerDAO();
            }
        } catch (Exception e) {
            System.out.println(" Database connection failed: " + e.getMessage());
            System.out.println(" Falling back to MockPlayerDAO with sample data");
        }

        // If database connection fails, use mock data
        System.out.println(" Using MockPlayerDAO - no database connection required");
        return new MockPlayerDAO();
    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(" Server started successfully on port " + PORT);
            System.out.println(" Waiting for client connections...");

            // Display which DAO we're using
            if (playerDAO instanceof MockPlayerDAO) {
                System.out.println(" Running in OFFLINE MODE with mock data");
                System.out.println(" Connect to Dkit WiFi for database access");
            } else {
                System.out.println(" Running in DATABASE MODE with real MySQL");
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 New client connected: " + clientSocket.getInetAddress());
                executorService.execute(new ClientHandler(clientSocket, playerDAO));
            }
        } catch (IOException e) {
            System.err.println(" Server error: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final IPlayerDAO playerDAO;

        public ClientHandler(Socket socket, IPlayerDAO playerDAO) {
            this.clientSocket = socket;
            this.playerDAO = playerDAO;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request == null || request.trim().isEmpty()) {
                    System.out.println("⚠ Received empty request from client");
                    return;
                }

                System.out.println(" Received request: " + request);

                JSONObject requestJson = new JSONObject(request);
                String command = requestJson.getString("command");
                JSONObject responseJson = new JSONObject();

                switch (command) {
                    case "getAllPlayers":
                        System.out.println(" Processing: Get all players");
                        String allPlayersJson = playerDAO.findAllPlayersJson();
                        responseJson.put("status", "success");
                        responseJson.put("data", new JSONArray(allPlayersJson));
                        break;

                    case "getPlayerById":
                        int id = requestJson.getInt("id");
                        System.out.println(" Processing: Get player by ID " + id);
                        String playerJson = playerDAO.findPlayerByIdJson(id);
                        if (!playerJson.equals("{}")) {
                            responseJson.put("status", "success");
                            responseJson.put("data", new JSONObject(playerJson));
                        } else {
                            responseJson.put("status", "error");
                            responseJson.put("message", "Player not found with ID: " + id);
                        }
                        break;

                    case "insertPlayer":
                        System.out.println(" Processing: Insert new player");
                        JSONObject playerData = requestJson.getJSONObject("player");
                        com.gaa.player.dto.PlayerDTO playerDTO = new com.gaa.player.dto.PlayerDTO();

                        // Set player data from JSON
                        playerDTO.setName(playerData.getString("name"));
                        playerDTO.setAge(playerData.getInt("age"));
                        playerDTO.setHeight((float) playerData.getDouble("height"));
                        playerDTO.setWeight((float) playerData.getDouble("weight"));
                        playerDTO.setPosition(playerData.getString("position"));
                        playerDTO.setCounty(playerData.getString("county"));
                        playerDTO.setClub(playerData.getString("club"));
                        playerDTO.setGoalsScored(playerData.getInt("goalsScored"));
                        playerDTO.setPointsScored(playerData.getInt("pointsScored"));
                        playerDTO.setActive(playerData.getBoolean("isActive"));

                        com.gaa.player.dto.PlayerDTO insertedPlayer = playerDAO.insertPlayer(playerDTO);
                        if (insertedPlayer != null) {
                            responseJson.put("status", "success");
                            responseJson.put("data", new JSONObject(playerDAO.findPlayerByIdJson(insertedPlayer.getId())));
                            responseJson.put("message", "Player added successfully with ID: " + insertedPlayer.getId());
                        } else {
                            responseJson.put("status", "error");
                            responseJson.put("message", "Failed to insert player");
                        }
                        break;

                    case "deletePlayer":
                        int playerId = requestJson.getInt("id");
                        System.out.println(" Processing: Delete player ID " + playerId);
                        boolean deleted = playerDAO.deletePlayerById(playerId);
                        if (deleted) {
                            responseJson.put("status", "success");
                            responseJson.put("message", "Player deleted successfully");
                        } else {
                            responseJson.put("status", "error");
                            responseJson.put("message", "Player not found with ID: " + playerId);
                        }
                        break;

                    case "filterPlayers":
                        String filter = requestJson.getString("filter");
                        System.out.println(" Processing: Filter players with '" + filter + "'");
                        java.util.List<com.gaa.player.dto.PlayerDTO> filteredPlayers = playerDAO.findPlayersUsingFilter(filter);
                        JSONArray filteredArray = new JSONArray();
                        for (com.gaa.player.dto.PlayerDTO p : filteredPlayers) {
                            filteredArray.put(new JSONObject(playerDAO.findPlayerByIdJson(p.getId())));
                        }
                        responseJson.put("status", "success");
                        responseJson.put("data", filteredArray);
                        break;

                    default:
                        responseJson.put("status", "error");
                        responseJson.put("message", "Unknown command: " + command);
                        break;
                }

                out.println(responseJson.toString());

            } catch (IOException e) {
                System.err.println(" Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println(" Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}