package com.gaa.player.dao;

import com.gaa.player.dto.PlayerDTO;
import com.gaa.player.util.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerDAO implements IPlayerDAO {
    private Set<Integer> playerIdCache;

    public PlayerDAO() {
        this.playerIdCache = new HashSet<>();
        initializeCache();
    }

    private void initializeCache() {
        try {
            List<PlayerDTO> players = getAllPlayers();
            for (PlayerDTO player : players) {
                playerIdCache.add(player.getId());
            }
        } catch (Exception e) {
            System.err.println("Error initializing cache: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        List<PlayerDTO> players = new ArrayList<>();
        String query = "SELECT * FROM players";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                PlayerDTO player = new PlayerDTO();
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                player.setAge(rs.getInt("age"));
                player.setHeight(rs.getFloat("height"));
                player.setWeight(rs.getFloat("weight"));
                player.setPosition(rs.getString("position"));
                player.setCounty(rs.getString("county"));
                player.setClub(rs.getString("club"));
                player.setGoalsScored(rs.getInt("goals_scored"));
                player.setPointsScored(rs.getInt("points_scored"));
                player.setActive(rs.getBoolean("is_active"));
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all players: " + e.getMessage());
        }
        return players;
    }

    @Override
    public PlayerDTO getPlayerById(int id) {
        if (!playerIdCache.contains(id)) {
            return null;
        }

        String query = "SELECT * FROM players WHERE id = ?";
        PlayerDTO player = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    player = new PlayerDTO();
                    player.setId(rs.getInt("id"));
                    player.setName(rs.getString("name"));
                    player.setAge(rs.getInt("age"));
                    player.setHeight(rs.getFloat("height"));
                    player.setWeight(rs.getFloat("weight"));
                    player.setPosition(rs.getString("position"));
                    player.setCounty(rs.getString("county"));
                    player.setClub(rs.getString("club"));
                    player.setGoalsScored(rs.getInt("goals_scored"));
                    player.setPointsScored(rs.getInt("points_scored"));
                    player.setActive(rs.getBoolean("is_active"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting player by ID: " + e.getMessage());
        }
        return player;
    }

    @Override
    public boolean deletePlayerById(int id) {
        String query = "DELETE FROM players WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                playerIdCache.remove(id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting player: " + e.getMessage());
        }
        return false;
    }

    @Override
    public PlayerDTO insertPlayer(PlayerDTO player) {
        String query = "INSERT INTO players (name, age, height, weight, position, county, club, goals_scored, points_scored, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, player.getName());
            pstmt.setInt(2, player.getAge());
            pstmt.setFloat(3, player.getHeight());
            pstmt.setFloat(4, player.getWeight());
            pstmt.setString(5, player.getPosition());
            pstmt.setString(6, player.getCounty());
            pstmt.setString(7, player.getClub());
            pstmt.setInt(8, player.getGoalsScored());
            pstmt.setInt(9, player.getPointsScored());
            pstmt.setBoolean(10, player.isActive());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        player.setId(newId);
                        playerIdCache.add(newId);
                        return player;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting player: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PlayerDTO> findPlayersUsingFilter(String filter) {
        List<PlayerDTO> filteredPlayers = new ArrayList<>();
        String query = "SELECT * FROM players WHERE county LIKE ? OR club LIKE ? OR position LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + filter + "%");
            pstmt.setString(2, "%" + filter + "%");
            pstmt.setString(3, "%" + filter + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PlayerDTO player = new PlayerDTO();
                    player.setId(rs.getInt("id"));
                    player.setName(rs.getString("name"));
                    player.setAge(rs.getInt("age"));
                    player.setHeight(rs.getFloat("height"));
                    player.setWeight(rs.getFloat("weight"));
                    player.setPosition(rs.getString("position"));
                    player.setCounty(rs.getString("county"));
                    player.setClub(rs.getString("club"));
                    player.setGoalsScored(rs.getInt("goals_scored"));
                    player.setPointsScored(rs.getInt("points_scored"));
                    player.setActive(rs.getBoolean("is_active"));
                    filteredPlayers.add(player);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering players: " + e.getMessage());
        }
        return filteredPlayers;
    }

    @Override
    public String findAllPlayersJson() {
        List<PlayerDTO> players = getAllPlayers();
        JSONArray jsonArray = new JSONArray();

        for (PlayerDTO player : players) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", player.getId());
            jsonObject.put("name", player.getName());
            jsonObject.put("age", player.getAge());
            jsonObject.put("height", player.getHeight());
            jsonObject.put("weight", player.getWeight());
            jsonObject.put("position", player.getPosition());
            jsonObject.put("county", player.getCounty());
            jsonObject.put("club", player.getClub());
            jsonObject.put("goalsScored", player.getGoalsScored());
            jsonObject.put("pointsScored", player.getPointsScored());
            jsonObject.put("isActive", player.isActive());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    @Override
    public String findPlayerByIdJson(int id) {
        PlayerDTO player = getPlayerById(id);
        if (player == null) {
            return "{}";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", player.getId());
        jsonObject.put("name", player.getName());
        jsonObject.put("age", player.getAge());
        jsonObject.put("height", player.getHeight());
        jsonObject.put("weight", player.getWeight());
        jsonObject.put("position", player.getPosition());
        jsonObject.put("county", player.getCounty());
        jsonObject.put("club", player.getClub());
        jsonObject.put("goalsScored", player.getGoalsScored());
        jsonObject.put("pointsScored", player.getPointsScored());
        jsonObject.put("isActive", player.isActive());

        return jsonObject.toString();
    }
}