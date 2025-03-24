package com.kombat3.kombat3.dto;

import com.kombat3.kombat3.model.GameMode;

public class GameCreationDTO {
    private String hostPlayerName;
    private GameMode gameMode;

    public GameCreationDTO() {
    }

    public GameCreationDTO(String hostPlayerName, GameMode gameMode) {
        this.hostPlayerName = hostPlayerName;
        this.gameMode = gameMode;
    }

    public String getHostPlayerName() {
        return hostPlayerName;
    }

    public void setHostPlayerName(String hostPlayerName) {
        this.hostPlayerName = hostPlayerName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
