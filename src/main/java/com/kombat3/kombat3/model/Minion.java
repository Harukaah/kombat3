package com.kombat3.kombat3.model;

public class Minion {
    private String id;         // Unique ID
    private String name;       // e.g., "Soldier", "Tank", etc.
    private int defFactor;     // Defense factor (user-chosen)
    private int atk;           // Attack value (predetermined or set up for each minion type)
    private long cost;         // Cost to purchase (if relevant)
    private double hp;         // Current HP
    private String ownerId;    // ID of the owning player
    private long spawnOrder;   // For controlling "oldest to newest" movement/attack

    public Minion() {
    }

    public Minion(String id, String name, int defFactor, int atk, long cost, double hp,
                  String ownerId, long spawnOrder) {
        this.id = id;
        this.name = name;
        this.defFactor = defFactor;
        this.atk = atk;
        this.cost = cost;
        this.hp = hp;
        this.ownerId = ownerId;
        this.spawnOrder = spawnOrder;
    }

    // Getters and setters...

    public boolean isAlive() {
        return hp > 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDefFactor() {
        return defFactor;
    }

    public void setDefFactor(int defFactor) {
        this.defFactor = defFactor;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public long getSpawnOrder() {
        return spawnOrder;
    }

    public void setSpawnOrder(long spawnOrder) {
        this.spawnOrder = spawnOrder;
    }
}
