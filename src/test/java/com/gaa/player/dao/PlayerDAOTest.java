package com.gaa.player.dao;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDAOTest {
    private static PlayerDAO playerDAO;
    private static PlayerDTO.PlayerDTO testPlayer;

    @BeforeAll
    static void setUp() {
        playerDAO = new PlayerDAO();

        // Create a test player
        testPlayer = new PlayerDTO.PlayerDTO();
        testPlayer.setName("Test Player");
        testPlayer.setAge(25);
        testPlayer.setHeight(1.85f);
        testPlayer.setWeight(80.5f);
        testPlayer.setPosition("Midfield");
        testPlayer.setCounty("Test County");
        testPlayer.setClub("Test Club");
        testPlayer.setGoalsScored(5);
        testPlayer.setPointsScored(15);
        testPlayer.setActive(true);

        // Insert the test player
        testPlayer = playerDAO.insertPlayer(testPlayer);
    }

    @AfterAll
    static void tearDown() {
        // Clean up by deleting the test player
        if (testPlayer != null) {
            playerDAO.deletePlayerById(testPlayer.getId());
        }
    }

    @Test
    void getAllPlayers() {
        List<PlayerDTO.PlayerDTO> players = playerDAO.getAllPlayers();
        assertNotNull(players);
        assertFalse(players.isEmpty());
    }

    @Test
    void getPlayerById() {
        PlayerDTO.PlayerDTO player = playerDAO.getPlayerById(testPlayer.getId());
        assertNotNull(player);
        assertEquals(testPlayer.getName(), player.getName());
    }

    @Test
    void getPlayerById_NotFound() {
        PlayerDTO.PlayerDTO player = playerDAO.getPlayerById(-1);
        assertNull(player);
    }

    @Test
    void insertPlayer() {
        PlayerDTO.PlayerDTO newPlayer = new PlayerDTO.PlayerDTO();
        newPlayer.setName("New Test Player");
        newPlayer.setAge(22);
        newPlayer.setHeight(1.78f);
        newPlayer.setWeight(75.0f);
        newPlayer.setPosition("Forward");
        newPlayer.setCounty("New County");
        newPlayer.setClub("New Club");
        newPlayer.setGoalsScored(3);
        newPlayer.setPointsScored(10);
        newPlayer.setActive(true);

        PlayerDTO.PlayerDTO insertedPlayer = playerDAO.insertPlayer(newPlayer);
        assertNotNull(insertedPlayer);
        assertTrue(insertedPlayer.getId() > 0);

        // Clean up
        playerDAO.deletePlayerById(insertedPlayer.getId());
    }

    @Test
    void deletePlayerById() {
        // Create a player to delete
        PlayerDTO.PlayerDTO playerToDelete = new PlayerDTO.PlayerDTO();
        playerToDelete.setName("Delete Test Player");
        playerToDelete.setAge(30);
        playerToDelete.setHeight(1.90f);
        playerToDelete.setWeight(85.0f);
        playerToDelete.setPosition("Defender");
        playerToDelete.setCounty("Delete County");
        playerToDelete.setClub("Delete Club");
        playerToDelete.setGoalsScored(1);
        playerToDelete.setPointsScored(2);
        playerToDelete.setActive(false);

        playerToDelete = playerDAO.insertPlayer(playerToDelete);
        assertTrue(playerDAO.deletePlayerById(playerToDelete.getId()));

        // Verify deletion
        assertNull(playerDAO.getPlayerById(playerToDelete.getId()));
    }

    @Test
    void findPlayersUsingFilter() {
        List<PlayerDTO.PlayerDTO> players = playerDAO.findPlayersUsingFilter(testPlayer.getCounty());
        assertNotNull(players);
        assertFalse(players.isEmpty());
        assertTrue(players.stream().anyMatch(p -> p.getCounty().equals(testPlayer.getCounty())));
    }

    @Test
    void findAllPlayersJson() {
        String json = playerDAO.findAllPlayersJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    void findPlayerByIdJson() {
        String json = playerDAO.findPlayerByIdJson(testPlayer.getId());
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }
}