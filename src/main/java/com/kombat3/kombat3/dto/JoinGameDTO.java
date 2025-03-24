package com.kombat3.kombat3.dto;

public class JoinGameDTO {
    private String gameId;
    private String playerName;

    public JoinGameDTO() {
    }

    public JoinGameDTO(String gameId, String playerName) {
        this.gameId = gameId;
        this.playerName = playerName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
