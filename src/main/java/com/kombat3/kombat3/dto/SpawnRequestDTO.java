package com.kombat3.kombat3.dto;

public class SpawnRequestDTO {
    private String gameId;
    private String playerId;
    private String minionName;
    private int defFactor;
    private int row;
    private int col;

    public SpawnRequestDTO() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getMinionName() {
        return minionName;
    }

    public void setMinionName(String minionName) {
        this.minionName = minionName;
    }

    public int getDefFactor() {
        return defFactor;
    }

    public void setDefFactor(int defFactor) {
        this.defFactor = defFactor;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
