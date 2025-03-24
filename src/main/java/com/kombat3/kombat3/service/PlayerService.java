package com.kombat3.kombat3.service;

import com.kombat3.kombat3.model.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerService {

    public void spawnMinion(Game game, Player player, String minionName, int defFactor, int row, int col) {
        // Basic validations
        if (game.isFinished()) {
            throw new IllegalStateException("Game is already finished. Cannot spawn.");
        }
        if (!isPlayerTurn(game, player.getPlayerId())) {
            throw new IllegalStateException("Not your turn to spawn.");
        }

        GameConfiguration config = game.getConfig();

        // Check if player can still spawn
        if (player.getSpawnsUsed() >= config.getMaxSpawns()) {
            throw new IllegalStateException("Max spawns reached for this player.");
        }

        // Check if the hex is in player's spawnable area
        HexCell target = game.getBoard()[row][col];
        if (!player.getSpawnableHexes().contains(target)) {
            throw new IllegalArgumentException("Cannot spawn in this hex, not in your spawnable area.");
        }
        if (!target.isEmpty()) {
            throw new IllegalArgumentException("Hex is already occupied.");
        }

        // Check cost
        if (player.getBudget() < config.getSpawnCost()) {
            throw new IllegalArgumentException("Not enough budget to spawn.");
        }

        // Deduct spawn cost
        player.setBudget(player.getBudget() - config.getSpawnCost());

        // Create new minion
        int atk = getPredefinedAttack(minionName);
        long purchaseCost = getPredefinedCost(minionName);
        double hp = config.getInitHp();

        // Keep track of global spawn order so we can move from oldest to newest
        game.incrementGlobalSpawnCounter();

        Minion minion = new Minion(
                UUID.randomUUID().toString(),
                minionName,
                defFactor,
                atk,
                purchaseCost,
                hp,
                player.getPlayerId(),
                game.getGlobalSpawnCounter()
        );

        // Place on board
        target.setOccupant(minion);
        // Add to player's list
        player.addMinion(minion);
        // Increment spawns used
        player.setSpawnsUsed(player.getSpawnsUsed() + 1);
    }

    public void purchaseHex(Game game, Player player, int row, int col) {
        if (game.isFinished()) {
            throw new IllegalStateException("Game is already finished. Cannot purchase hex.");
        }
        if (!isPlayerTurn(game, player.getPlayerId())) {
            throw new IllegalStateException("Not your turn to purchase a hex.");
        }

        GameConfiguration config = game.getConfig();
        HexCell target = game.getBoard()[row][col];
        if (!target.isEmpty()) {
            throw new IllegalArgumentException("Cannot purchase an occupied hex.");
        }

        // Must be adjacent to any existing spawnable hex
        boolean adjacent = player.getSpawnableHexes().stream()
                .anyMatch(hex -> isHexAdjacent(hex.getRow(), hex.getCol(), row, col));
        if (!adjacent) {
            throw new IllegalArgumentException("Hex is not adjacent to existing spawnable area.");
        }

        // Check cost
        if (player.getBudget() < config.getHexPurchaseCost()) {
            throw new IllegalArgumentException("Not enough budget to purchase hex.");
        }

        // Deduct cost
        player.setBudget(player.getBudget() - config.getHexPurchaseCost());
        // Add to player's spawnable hexes
        player.addSpawnableHex(target);
    }

    private boolean isHexAdjacent(int r1, int c1, int r2, int c2) {
        // For a hex grid with "odd-r" or "even-r" layout, adjacency can vary.
        // Here, we do a simple approach based on the given "skew" pattern.
        // We assume each hex has up to 6 neighbors:
        // For a standard "pointy top" or "flat top" layout, define carefully.
        // Below is a simplified approach for the 8x8 skewed grid:

        // We'll define possible neighbor offsets for an "odd-r" horizontal layout:
        // This might differ if row is even or odd. We adapt to a simpler approach:
        int rowDiff = Math.abs(r1 - r2);
        int colDiff = Math.abs(c1 - c2);

        // Accept adjacency if they're within 1 step in row/col, but not the same cell
        // (In a real hex layout, you might have 6 neighbors. This approach is simplified.)
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }
        if (rowDiff == 0 && colDiff == 0) {
            return false; // same cell
        }
        return true;
    }

    private boolean isPlayerTurn(Game game, String playerId) {
        int idx = game.getCurrentPlayerIndex();
        String currentPlayerId = game.getPlayerOrder().get(idx);
        return currentPlayerId.equals(playerId);
    }

    private int getPredefinedAttack(String minionName) {
        // Example logic:
        switch (minionName.toLowerCase()) {
            case "soldier": return 10;
            case "archer":  return 8;
            case "tank":    return 15;
            case "mage":    return 12;
            case "rogue":   return 9;
            default:        return 5;
        }
    }

    private long getPredefinedCost(String minionName) {
        // Example logic:
        switch (minionName.toLowerCase()) {
            case "soldier": return 150;
            case "archer":  return 120;
            case "tank":    return 300;
            case "mage":    return 200;
            case "rogue":   return 180;
            default:        return 100;
        }
    }
}

