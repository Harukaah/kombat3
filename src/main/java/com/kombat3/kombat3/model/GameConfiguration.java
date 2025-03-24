package com.kombat3.kombat3.model;

public class GameConfiguration {
    private long spawnCost;
    private long hexPurchaseCost;
    private long initBudget;
    private long initHp;
    private long turnBudget;
    private long maxBudget;
    private long interestPct;
    private long maxTurns;
    private long maxSpawns;

    public GameConfiguration() {
    }

    public GameConfiguration(long spawnCost, long hexPurchaseCost, long initBudget, long initHp,
                             long turnBudget, long maxBudget, long interestPct,
                             long maxTurns, long maxSpawns) {
        this.spawnCost = spawnCost;
        this.hexPurchaseCost = hexPurchaseCost;
        this.initBudget = initBudget;
        this.initHp = initHp;
        this.turnBudget = turnBudget;
        this.maxBudget = maxBudget;
        this.interestPct = interestPct;
        this.maxTurns = maxTurns;
        this.maxSpawns = maxSpawns;
    }

    // Getters and Setters
    public long getSpawnCost() {
        return spawnCost;
    }

    public void setSpawnCost(long spawnCost) {
        this.spawnCost = spawnCost;
    }

    public long getHexPurchaseCost() {
        return hexPurchaseCost;
    }

    public void setHexPurchaseCost(long hexPurchaseCost) {
        this.hexPurchaseCost = hexPurchaseCost;
    }

    public long getInitBudget() {
        return initBudget;
    }

    public void setInitBudget(long initBudget) {
        this.initBudget = initBudget;
    }

    public long getInitHp() {
        return initHp;
    }

    public void setInitHp(long initHp) {
        this.initHp = initHp;
    }

    public long getTurnBudget() {
        return turnBudget;
    }

    public void setTurnBudget(long turnBudget) {
        this.turnBudget = turnBudget;
    }

    public long getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(long maxBudget) {
        this.maxBudget = maxBudget;
    }

    public long getInterestPct() {
        return interestPct;
    }

    public void setInterestPct(long interestPct) {
        this.interestPct = interestPct;
    }

    public long getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(long maxTurns) {
        this.maxTurns = maxTurns;
    }

    public long getMaxSpawns() {
        return maxSpawns;
    }

    public void setMaxSpawns(long maxSpawns) {
        this.maxSpawns = maxSpawns;
    }
}
