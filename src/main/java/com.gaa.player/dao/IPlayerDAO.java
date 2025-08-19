package com.gaa.player.dao;

import com.gaa.player.dto.PlayerDTO;
import java.util.List;

public interface IPlayerDAO {
    List<PlayerDTO> getAllPlayers();
    PlayerDTO getPlayerById(int id);
    boolean deletePlayerById(int id);
    PlayerDTO insertPlayer(PlayerDTO player);
    List<PlayerDTO> findPlayersUsingFilter(String filter);
    String findAllPlayersJson();
    String findPlayerByIdJson(int id);
}