package com.kombat3.kombat3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kombat")
public class GameParamsConfig {

    private long spawnCost;
    private long hexPurchaseCost;
    private long initBudget;
    private long initHp;
    private long turnBudget;
    private long maxBudget;
    private long interestPct;
    private long maxTurns;
    private long maxSpawns;

    // Getters and setters
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
