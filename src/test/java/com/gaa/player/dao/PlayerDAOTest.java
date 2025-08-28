package com.gaa.player.dao;

import com.gaa.player.dto.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerDAOTest {

    private IPlayerDAO playerDAO;

    @BeforeEach
    void setUp() {
        playerDAO = new MockPlayerDAO(); // Use mock for testing
    }

    @Test
    void testGetAllPlayers() {
        // When
        List<PlayerDTO> players = playerDAO.getAllPlayers();

        // Then
        assertNotNull(players, "Player list should not be null");
        assertFalse(players.isEmpty(), "Player list should not be empty");
        assertEquals(15, players.size(), "Should return 15 sample players");

        // Verify first player data
        PlayerDTO firstPlayer = players.get(0);
        assertEquals("Con O Callaghan", firstPlayer.getName());
        assertEquals("Dublin", firstPlayer.getCounty());
        assertEquals(26, firstPlayer.getAge());
    }

    @Test
    void testGetPlayerById_ExistingPlayer() {
        // When
        PlayerDTO player = playerDAO.getPlayerById(1);

        // Then
        assertNotNull(player, "Player should not be null");
        assertEquals(1, player.getId());
        assertEquals("Con O Callaghan", player.getName());
    }

    @Test
    void testGetPlayerById_NonExistentPlayer() {
        // When
        PlayerDTO player = playerDAO.getPlayerById(999);

        // Then
        assertNull(player, "Should return null for non-existent player");
    }

    @Test
    void testInsertPlayer() {
        // Given
        PlayerDTO newPlayer = new PlayerDTO();
        newPlayer.setName("Test Player");
        newPlayer.setAge(22);
        newPlayer.setHeight(1.85f);
        newPlayer.setWeight(80.0f);
        newPlayer.setPosition("Forward");
        newPlayer.setCounty("Test County");
        newPlayer.setClub("Test Club");
        newPlayer.setGoalsScored(5);
        newPlayer.setPointsScored(10);
        newPlayer.setActive(true);

        // When
        PlayerDTO insertedPlayer = playerDAO.insertPlayer(newPlayer);

        // Then
        assertNotNull(insertedPlayer, "Inserted player should not be null");
        assertTrue(insertedPlayer.getId() > 0, "Should have generated ID");
        assertEquals("Test Player", insertedPlayer.getName());

        // Verify player was added to list
        List<PlayerDTO> players = playerDAO.getAllPlayers();
        assertEquals(16, players.size(), "Should have 16 players after insertion");
    }

    @Test
    void testDeletePlayerById_ExistingPlayer() {
        // When
        boolean deleted = playerDAO.deletePlayerById(1);

        // Then
        assertTrue(deleted, "Should return true for successful deletion");

        // Verify player was removed
        PlayerDTO player = playerDAO.getPlayerById(1);
        assertNull(player, "Player should be deleted");
    }

    @Test
    void testDeletePlayerById_NonExistentPlayer() {
        // When
        boolean deleted = playerDAO.deletePlayerById(999);

        // Then
        assertFalse(deleted, "Should return false for non-existent player");
    }

    @Test
    void testFindPlayersUsingFilter() {
        // When
        List<PlayerDTO> dublinPlayers = playerDAO.findPlayersUsingFilter("Dublin");
        List<PlayerDTO> kerryPlayers = playerDAO.findPlayersUsingFilter("Kerry");
        List<PlayerDTO> forwardPlayers = playerDAO.findPlayersUsingFilter("Forward");

        // Then
        assertNotNull(dublinPlayers);
        assertNotNull(kerryPlayers);
        assertNotNull(forwardPlayers);

        // Should find players from Dublin
        assertTrue(dublinPlayers.size() > 0);
        assertEquals("Dublin", dublinPlayers.get(0).getCounty());

        // Should find players from Kerry
        assertTrue(kerryPlayers.size() > 0);
        assertEquals("Kerry", kerryPlayers.get(0).getCounty());
    }

    @Test
    void testFindAllPlayersJson() {
        // When
        String json = playerDAO.findAllPlayersJson();

        // Then
        assertNotNull(json, "JSON should not be null");
        assertFalse(json.isEmpty(), "JSON should not be empty");
        assertTrue(json.contains("Con O Callaghan"), "JSON should contain player data");
    }

    @Test
    void testFindPlayerByIdJson() {
        // When
        String json = playerDAO.findPlayerByIdJson(1);
        String emptyJson = playerDAO.findPlayerByIdJson(999);

        // Then
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.contains("Con O Callaghan"));
        assertEquals("{}", emptyJson, "Should return empty JSON for non-existent player");
    }
}