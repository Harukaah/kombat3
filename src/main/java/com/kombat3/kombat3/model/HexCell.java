package com.kombat3.kombat3.model;

public class HexCell {
    private int row;
    private int col;
    private Minion occupant;  // The minion occupying this hex, if any

    public HexCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Minion getOccupant() {
        return occupant;
    }

    public void setOccupant(Minion occupant) {
        this.occupant = occupant;
    }

    public boolean isEmpty() {
        return occupant == null;
    }
}
