package com.kombat3.kombat3.model;

public class WinnerInfo {
    private String winnerPlayerId;
    private boolean tie;

    public WinnerInfo() {
    }

    public WinnerInfo(String winnerPlayerId, boolean tie) {
        this.winnerPlayerId = winnerPlayerId;
        this.tie = tie;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public void setWinnerPlayerId(String winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public boolean isTie() {
        return tie;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }
}
