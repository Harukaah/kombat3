package com.kombat3.kombat3.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private String gameId;
    private GameMode mode;
    private GameConfiguration config;
    private HexCell[][] board;            // 8x8 board
    private Map<String, Player> players;  // PlayerID -> Player
    private boolean started;
    private boolean finished;
    private int currentPlayerIndex;       // Which player's turn it is (index in playerOrder)
    private List<String> playerOrder;     // The order in which players take turns
    private WinnerInfo winnerInfo;        // If finished, store the result
    private long globalSpawnCounter;      // used to assign spawnOrder for minions

    public Game(String gameId, GameMode mode, GameConfiguration config) {
        this.gameId = gameId;
        this.mode = mode;
        this.config = config;
        this.board = new HexCell[8][8];
        this.players = new ConcurrentHashMap<>();
        this.started = false;
        this.finished = false;
        this.currentPlayerIndex = 0;
        this.globalSpawnCounter = 0;
        initBoard();
    }

    private void initBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = new HexCell(r, c);
            }
        }
    }

    // For frameworks
    public Game() {
    }

    public String getGameId() {
        return gameId;
    }

    public GameMode getMode() {
        return mode;
    }

    public GameConfiguration getConfig() {
        return config;
    }

    public HexCell[][] getBoard() {
        return board;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public List<String> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(List<String> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public WinnerInfo getWinnerInfo() {
        return winnerInfo;
    }

    public void setWinnerInfo(WinnerInfo winnerInfo) {
        this.winnerInfo = winnerInfo;
    }

    public long getGlobalSpawnCounter() {
        return globalSpawnCounter;
    }

    public void incrementGlobalSpawnCounter() {
        this.globalSpawnCounter++;
    }
}
