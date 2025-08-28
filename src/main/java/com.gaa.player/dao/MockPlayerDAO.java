package com.gaa.player.dao;

import com.gaa.player.dto.PlayerDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MockPlayerDAO implements IPlayerDAO {

    private List<PlayerDTO> mockPlayers;

    public MockPlayerDAO() {
        this.mockPlayers = new ArrayList<>();
        createSampleData();
        System.out.println(" Mock DAO initialized with " + mockPlayers.size() + " sample players");
    }

    private void createSampleData() {
        // Create sample GAA players data matching your database structure
        addPlayer(1, "Con O Callaghan", 26, 1.88f, 85.5f, "Full Forward", "Dublin", "Cuala", 12, 45, true);
        addPlayer(2, "David Clifford", 23, 1.88f, 83.0f, "Full Forward", "Kerry", "Fossa", 15, 52, true);
        addPlayer(3, "Shane Walsh", 28, 1.83f, 80.0f, "Half Forward", "Galway", "Kilmacud Crokes", 8, 38, true);
        addPlayer(4, "Brian Fenton", 29, 1.91f, 87.0f, "Midfield", "Dublin", "Raheny", 2, 15, true);
        addPlayer(5, "Sean O Shea", 24, 1.83f, 78.5f, "Half Forward", "Kerry", "Kenmare", 10, 42, true);
        addPlayer(6, "Cillian O Connor", 30, 1.83f, 82.0f, "Full Forward", "Mayo", "Ballintubber", 18, 60, true);
        addPlayer(7, "Lee Keegan", 32, 1.83f, 83.0f, "Half Back", "Mayo", "Westport", 1, 5, false);
        addPlayer(8, "James McCarthy", 31, 1.85f, 84.0f, "Half Back", "Dublin", "Ballymun Kickhams", 0, 8, true);
        addPlayer(9, "Conor Glass", 25, 1.85f, 85.0f, "Midfield", "Derry", "Glen", 3, 12, true);
        addPlayer(10, "Rory Beggan", 30, 1.88f, 90.0f, "Goalkeeper", "Monaghan", "Scotstown", 0, 2, true);
        addPlayer(11, "Shane McGuigan", 25, 1.85f, 84.5f, "Full Forward", "Derry", "Slaughtneil", 9, 35, true);
        addPlayer(12, "Clifford Kelly", 27, 1.80f, 79.0f, "Corner Back", "Kerry", "Dr Crokes", 0, 1, true);
        addPlayer(13, "Niall Morgan", 31, 1.87f, 89.0f, "Goalkeeper", "Tyrone", "Edendork", 0, 3, true);
        addPlayer(14, "Jack McCaffrey", 29, 1.80f, 76.0f, "Wing Back", "Dublin", "Clontarf", 1, 4, false);
        addPlayer(15, "Ryan McHugh", 28, 1.75f, 72.0f, "Wing Forward", "Donegal", "Kilcar", 4, 18, true);
    }

    private void addPlayer(int id, String name, int age, float height, float weight,
                           String position, String county, String club,
                           int goalsScored, int pointsScored, boolean isActive) {
        PlayerDTO player = new PlayerDTO();
        player.setId(id);
        player.setName(name);
        player.setAge(age);
        player.setHeight(height);
        player.setWeight(weight);
        player.setPosition(position);
        player.setCounty(county);
        player.setClub(club);
        player.setGoalsScored(goalsScored);
        player.setPointsScored(pointsScored);
        player.setActive(isActive);
        mockPlayers.add(player);
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return new ArrayList<>(mockPlayers);
    }

    @Override
    public PlayerDTO getPlayerById(int id) {
        return mockPlayers.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean deletePlayerById(int id) {
        boolean removed = mockPlayers.removeIf(p -> p.getId() == id);
        if (removed) {
            System.out.println(" Mock: Player with ID " + id + " deleted");
        } else {
            System.out.println(" Mock: Player with ID " + id + " not found");
        }
        return removed;
    }

    @Override
    public PlayerDTO insertPlayer(PlayerDTO player) {
        // Generate new ID
        int newId = mockPlayers.stream()
                .mapToInt(PlayerDTO::getId)
                .max()
                .orElse(0) + 1;

        player.setId(newId);
        mockPlayers.add(player);
        System.out.println(" Mock: New player added with ID " + newId + " - " + player.getName());
        return player;
    }

    @Override
    public List<PlayerDTO> findPlayersUsingFilter(String filter) {
        List<PlayerDTO> results = new ArrayList<>();
        String lowerFilter = filter.toLowerCase();

        for (PlayerDTO player : mockPlayers) {
            if (player.getCounty().toLowerCase().contains(lowerFilter) ||
                    player.getClub().toLowerCase().contains(lowerFilter) ||
                    player.getPosition().toLowerCase().contains(lowerFilter) ||
                    player.getName().toLowerCase().contains(lowerFilter)) {
                results.add(player);
            }
        }

        System.out.println(" Mock: Filter '" + filter + "' found " + results.size() + " players");
        return results;
    }

    @Override
    public String findAllPlayersJson() {
        JSONArray jsonArray = new JSONArray();
        for (PlayerDTO player : mockPlayers) {
            jsonArray.put(convertToJson(player));
        }
        String result = jsonArray.toString();
        System.out.println(" Mock: Generated JSON for " + mockPlayers.size() + " players");
        return result;
    }

    @Override
    public String findPlayerByIdJson(int id) {
        PlayerDTO player = getPlayerById(id);
        if (player != null) {
            System.out.println(" Mock: Found player JSON for ID " + id);
            return convertToJson(player).toString();
        } else {
            System.out.println(" Mock: No player found for ID " + id);
            return "{}";
        }
    }

    private JSONObject convertToJson(PlayerDTO player) {
        JSONObject json = new JSONObject();
        json.put("id", player.getId());
        json.put("name", player.getName());
        json.put("age", player.getAge());
        json.put("height", player.getHeight());
        json.put("weight", player.getWeight());
        json.put("position", player.getPosition());
        json.put("county", player.getCounty());
        json.put("club", player.getClub());
        json.put("goalsScored", player.getGoalsScored());
        json.put("pointsScored", player.getPointsScored());
        json.put("isActive", player.isActive());
        return json;
    }

    // Helper method to display all players (for debugging)
    public void displayAllPlayers() {
        System.out.println("\n Mock Player Database Contents:");
        System.out.println("==============================================");
        System.out.printf("| %-2s | %-20s | %-3s | %-10s | %-6s |%n",
                "ID", "Name", "Age", "County", "Active");
        System.out.println("==============================================");

        for (PlayerDTO player : mockPlayers) {
            System.out.printf("| %-2d | %-20s | %-3d | %-10s | %-6s |%n",
                    player.getId(),
                    player.getName(),
                    player.getAge(),
                    player.getCounty(),
                    player.isActive() ? "Yes" : "No");
        }
        System.out.println("==============================================");
    }
}