package com.gaa.player.server;

import com.gaa.player.dao.MockPlayerDAO;
import com.gaa.player.dao.IPlayerDAO;
import com.gaa.player.dao.PlayerDAO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8888;
    private static final int THREAD_POOL_SIZE = 10;
    private final IPlayerDAO playerDAO;

    public Server() {
        this.playerDAO = new PlayerDAO();

    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new ClientHandler(clientSocket, playerDAO));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
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
                System.out.println("Received request: " + request);

                JSONObject requestJson = new JSONObject(request);
                String command = requestJson.getString("command");
                JSONObject responseJson = new JSONObject();

                switch (command) {
                    case "getAllPlayers":
                        String allPlayersJson = playerDAO.findAllPlayersJson();
                        responseJson.put("status", "success");
                        responseJson.put("data", new JSONArray(allPlayersJson));
                        break;

                    case "getPlayerById":
                        int id = requestJson.getInt("id");
                        String playerJson = playerDAO.findPlayerByIdJson(id);
                        responseJson.put("status", playerJson.equals("{}") ? "error" : "success");
                        responseJson.put("data", new JSONObject(playerJson));
                        break;

                    case "insertPlayer":
                        JSONObject playerData = requestJson.getJSONObject("player");
                        com.gaa.player.dto.PlayerDTO playerDTO = new com.gaa.player.dto.PlayerDTO();
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
                        } else {
                            responseJson.put("status", "error");
                            responseJson.put("message", "Failed to insert player");
                        }
                        break;

                    case "deletePlayer":
                        int playerId = requestJson.getInt("id");
                        boolean deleted = playerDAO.deletePlayerById(playerId);
                        responseJson.put("status", deleted ? "success" : "error");
                        responseJson.put("message", deleted ? "Player deleted successfully" : "Failed to delete player");
                        break;

                    case "filterPlayers":
                        String filter = requestJson.getString("filter");
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
                        responseJson.put("message", "Unknown command");
                        break;
                }

                out.println(responseJson.toString());
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}