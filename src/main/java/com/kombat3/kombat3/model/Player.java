package com.kombat3.kombat3.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerId;
    private String playerName;
    private double budget;                // Must be tracked as double for interest calculations
    private List<Minion> minions;         // All minions belonging to this player
    private List<HexCell> spawnableHexes; // Hexes where this player can spawn new minions
    private int turnsPlayed;
    private int spawnsUsed;               // How many spawns used so far

    public Player(String playerId, String playerName, double initBudget, HexCell initialSpawnHex) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.budget = initBudget;
        this.minions = new ArrayList<>();
        this.spawnableHexes = new ArrayList<>();
        this.turnsPlayed = 0;
        this.spawnsUsed = 0;

        if (initialSpawnHex != null) {
            spawnableHexes.add(initialSpawnHex);
        }
    }

    // For frameworks
    public Player() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public List<Minion> getMinions() {
        return minions;
    }

    public List<HexCell> getSpawnableHexes() {
        return spawnableHexes;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    public void setTurnsPlayed(int turnsPlayed) {
        this.turnsPlayed = turnsPlayed;
    }

    public int getSpawnsUsed() {
        return spawnsUsed;
    }

    public void setSpawnsUsed(int spawnsUsed) {
        this.spawnsUsed = spawnsUsed;
    }

    public void addMinion(Minion m) {
        minions.add(m);
    }

    public void removeMinion(Minion m) {
        minions.remove(m);
    }

    public boolean hasAliveMinions() {
        return minions.stream().anyMatch(Minion::isAlive);
    }

    public void addSpawnableHex(HexCell hex) {
        spawnableHexes.add(hex);
    }
}
