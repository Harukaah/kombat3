package com.kombat3.kombat3.service;

import com.kombat3.kombat3.model.Game;
import com.kombat3.kombat3.model.HexCell;
import com.kombat3.kombat3.model.Minion;
import com.kombat3.kombat3.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovementService {

    /**
     * Move and/or attack with each minion (oldest to newest).
     * Each minion attempts to move closer to the nearest enemy minion.
     * If already adjacent, it attacks.
     */
    public void moveAndAttackAllMinions(Game game, Player currentPlayer) {
        // Sort minions by spawnOrder (oldest first)
        List<Minion> sortedMinions = new ArrayList<>(currentPlayer.getMinions());
        sortedMinions.sort(Comparator.comparingLong(Minion::getSpawnOrder));

        for (Minion minion : sortedMinions) {
            if (!minion.isAlive()) {
                continue;
            }
            // If there's an adjacent enemy, attack it
            if (attackIfEnemyAdjacent(game, minion)) {
                continue; // Attack used up the minion's action
            }

            // Otherwise, try to move closer to the nearest enemy
            moveOneStepTowardsNearestEnemy(game, minion);
            // After moving, check again if an enemy is adjacent, then attack
            attackIfEnemyAdjacent(game, minion);
        }
    }

    /**
     * Attempt to move the minion one step closer to the nearest enemy.
     */
    private void moveOneStepTowardsNearestEnemy(Game game, Minion minion) {
        HexCell currentCell = findMinionCell(game, minion);
        if (currentCell == null) return; // Should never happen

        // Find the nearest enemy minion
        Minion nearestEnemy = findNearestEnemyMinion(game, minion);
        if (nearestEnemy == null) {
            return; // No enemies to move towards
        }

        HexCell enemyCell = findMinionCell(game, nearestEnemy);
        if (enemyCell == null) {
            return; // Enemy not on board?
        }

        // If we're already adjacent, do not move
        if (areCellsAdjacent(game, currentCell, enemyCell)) {
            return;
        }

        // Use BFS to get the next step toward the enemy
        HexCell nextStep = getNextStepBFS(game, currentCell, enemyCell);
        if (nextStep != null && nextStep.isEmpty()) {
            // Move the minion: clear current cell and set the occupant in the next cell
            currentCell.setOccupant(null);
            nextStep.setOccupant(minion);
        }
    }

    /**
     * Find the next cell on a shortest path from start to goal using BFS.
     * Returns the immediate next step.
     */
    private HexCell getNextStepBFS(Game game, HexCell start, HexCell goal) {
        if (start == goal) return null;

        int rows = 8, cols = 8;
        boolean[][] visited = new boolean[rows][cols];
        Map<HexCell, HexCell> parent = new HashMap<>();

        Queue<HexCell> queue = new LinkedList<>();
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        boolean found = false;
        while (!queue.isEmpty()) {
            HexCell current = queue.poll();
            if (current == goal) {
                found = true;
                break;
            }
            for (HexCell neighbor : getNeighbors(game, current)) {
                if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                    visited[neighbor.getRow()][neighbor.getCol()] = true;
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) return null;

        // Reconstruct path from goal to start
        List<HexCell> path = new ArrayList<>();
        HexCell cur = goal;
        while (cur != null && cur != start) {
            path.add(cur);
            cur = parent.get(cur);
        }
        if (cur == null) return null; // Should not happen
        path.add(start);
        Collections.reverse(path);
        // Return the next step from start (if exists)
        return (path.size() >= 2) ? path.get(1) : null;
    }

    /**
     * Return a list of neighboring hexes for a given cell (ignoring board edges).
     */
    private List<HexCell> getNeighbors(Game game, HexCell cell) {
        int r = cell.getRow();
        int c = cell.getCol();
        List<HexCell> neighbors = new ArrayList<>();

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                // Skip the cell itself
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8) {
                    neighbors.add(game.getBoard()[nr][nc]);
                }
            }
        }
        return neighbors;
    }

    private boolean areCellsAdjacent(Game game, HexCell a, HexCell b) {
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getCol() - b.getCol());
        return (rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0));
    }

    /**
     * Check if an enemy is adjacent to this minion's cell.
     * If so, attack the first enemy found.
     * Returns true if an attack was performed.
     */
    private boolean attackIfEnemyAdjacent(Game game, Minion minion) {
        HexCell currentCell = findMinionCell(game, minion);
        if (currentCell == null) return false;

        List<HexCell> neighbors = getNeighbors(game, currentCell);
        for (HexCell neighbor : neighbors) {
            Minion occupant = neighbor.getOccupant();
            if (occupant != null && !occupant.getOwnerId().equals(minion.getOwnerId()) && occupant.isAlive()) {
                // Attack the enemy and pass the game object for proper removal
                performAttack(minion, occupant, neighbor, game);
                return true;
            }
        }
        return false;
    }

    /**
     * Perform an attack: damage = max(0, attacker's atk - defender's defFactor).
     * If defender's HP falls to or below 0, remove it from the board and from its owner's minions.
     */
    private void performAttack(Minion attacker, Minion defender, HexCell defenderCell, Game game) {
        int damage = Math.max(0, attacker.getAtk() - defender.getDefFactor());
        double newHp = defender.getHp() - damage;
        defender.setHp(newHp);
        if (newHp <= 0) {
            // Remove the defeated minion from the board
            defenderCell.setOccupant(null);
            // Remove it from its owner's minion list using the game context
            removeDeadMinion(defender, game);
        }
    }

    /**
     * Remove a dead minion from its owner's list.
     */
    private void removeDeadMinion(Minion minion, Game game) {
        Player owner = game.getPlayers().get(minion.getOwnerId());
        if (owner != null) {
            owner.removeMinion(minion);
        }
    }

    /**
     * Return the cell that currently contains the given minion, or null if not found.
     */
    private HexCell findMinionCell(Game game, Minion minion) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (game.getBoard()[r][c].getOccupant() == minion) {
                    return game.getBoard()[r][c];
                }
            }
        }
        return null;
    }

    /**
     * Find the nearest enemy minion by checking all enemy minions and picking the one with the shortest distance.
     */
    private Minion findNearestEnemyMinion(Game game, Minion minion) {
        Player me = game.getPlayers().get(minion.getOwnerId());
        double bestDist = Double.MAX_VALUE;
        Minion best = null;

        HexCell myCell = findMinionCell(game, minion);
        if (myCell == null) return null;

        for (Player p : game.getPlayers().values()) {
            if (p == me) continue;
            for (Minion enemy : p.getMinions()) {
                if (!enemy.isAlive()) continue;
                HexCell enemyCell = findMinionCell(game, enemy);
                if (enemyCell == null) continue;
                double dist = distance(myCell, enemyCell);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = enemy;
                }
            }
        }
        return best;
    }

    /**
     * Calculate an approximate Euclidean distance between two hex cells.
     */
    private double distance(HexCell a, HexCell b) {
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getCol() - b.getCol());
        return Math.sqrt(rowDiff * rowDiff + colDiff * colDiff);
    }
}


