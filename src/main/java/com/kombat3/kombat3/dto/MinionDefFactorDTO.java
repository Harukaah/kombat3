package com.kombat3.kombat3.dto;

public class MinionDefFactorDTO {
    private String gameId;
    private String playerId;
    private String minionName; // e.g. "Soldier", "Archer", etc.
    private int defFactor;

    public MinionDefFactorDTO() {
    }

    public MinionDefFactorDTO(String gameId, String playerId, String minionName, int defFactor) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.minionName = minionName;
        this.defFactor = defFactor;
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
}
