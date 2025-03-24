package com.kombat3.kombat3.service;

import com.kombat3.kombat3.model.*;
import com.kombat3.kombat3.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TurnService {

    private final MovementService movementService;
    private final GameRepository gameRepository;

    public TurnService(MovementService movementService, GameRepository gameRepository) {
        this.movementService = movementService;
        this.gameRepository = gameRepository;
    }

    /**
     * Execute the current player's turn:
     * 1. Add turnBudget to player's budget
     * 2. Calculate interest
     * 3. (Spawn/purchase happen via separate endpoints, but only if it's still the same turn)
     * 4. Move and attack with minions
     * 5. Check if the game ended (enemy has no minions, or maxTurns reached)
     * 6. If not ended, pass turn to the next player (or handle bots if needed)
     */
    public synchronized Game nextTurn(String gameId) {
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        if (!game.isStarted()) {
            throw new IllegalStateException("Game not started.");
        }
        if (game.isFinished()) {
            throw new IllegalStateException("Game already finished.");
        }

        // Identify current player
        int idx = game.getCurrentPlayerIndex();
        String currentPlayerId = game.getPlayerOrder().get(idx);
        Player currentPlayer = game.getPlayers().get(currentPlayerId);

        // 1. Add turnBudget
        GameConfiguration config = game.getConfig();
        currentPlayer.setBudget(currentPlayer.getBudget() + config.getTurnBudget());

        // 2. Calculate interest
        currentPlayer.setTurnsPlayed(currentPlayer.getTurnsPlayed() + 1);
        applyInterest(game, currentPlayer);

        // 3. (Spawn/purchase can be done via separate calls before we finalize the turn)
        //    We'll assume the user called those endpoints already if they wanted to.

        // 4. Move and attack with minions
        movementService.moveAndAttackAllMinions(game, currentPlayer);

        // 4a. Cleanup any minions that died during other minions' actions
        //     (The movementService already removes them from the board,
        //      but let's ensure no references remain. It's done inside movementService.)

        // 5. Check if the game ended
        if (checkGameEndConditions(game)) {
            gameRepository.save(game);
            return game;
        }

        // If not ended, pass turn to the next player
        int nextIndex = (idx + 1) % game.getPlayerOrder().size();
        game.setCurrentPlayerIndex(nextIndex);

        // If in SOLITAIRE or AUTO mode, handle bot logic here (optional).
        if (game.getMode() == GameMode.SOLITAIRE && nextIndex == 1) {
            // The second player is a bot
            // For demonstration, we can do a minimal bot action
            handleBotTurn(game);
        } else if (game.getMode() == GameMode.AUTO) {
            // Both players are bots, so keep looping until we get back to a human or game ends
            // For demonstration, let's just do one bot turn
            handleBotTurn(game);
        }

        // After the bot's turn, we might need to check end conditions again
        checkGameEndConditions(game);

        gameRepository.save(game);
        return game;
    }

    private void applyInterest(Game game, Player player) {
        GameConfiguration config = game.getConfig();
        double m = player.getBudget();
        // if turn count is 0 or 1, ln(t) might be 0 or negative, skip interest
        if (player.getTurnsPlayed() < 1) {
            return;
        }
        double t = (double) player.getTurnsPlayed();
        double b = (double) config.getInterestPct(); // base interest rate
        if (m <= 0) {
            return;
        }

        // r = b * log10(m) * ln(t)
        double logM = Math.log10(m);
        if (logM <= 0) {
            return;
        }
        double lnT = Math.log(t);
        if (lnT <= 0) {
            return;
        }
        double r = b * logM * lnT; // This is the interest rate in %
        double interest = m * (r / 100.0);
        double newBudget = m + interest;

        // Cap at maxBudget
        if (newBudget > config.getMaxBudget()) {
            newBudget = config.getMaxBudget();
        }
        player.setBudget(newBudget);
    }

    /**
     * Check if game ended because a player has no minions or we reached maxTurns.
     * If ended, compute winner.
     */
    private boolean checkGameEndConditions(Game game) {
        // Check if any player has 0 alive minions
        List<Player> allPlayers = new ArrayList<>(game.getPlayers().values());
        // If exactly one player has alive minions, that player is the winner
        List<Player> alivePlayers = allPlayers.stream()
                .filter(Player::hasAliveMinions)
                .collect(Collectors.toList());

        if (alivePlayers.size() == 0) {
            // tie: no one has minions
            game.setFinished(true);
            game.setWinnerInfo(new WinnerInfo(null, true));
            return true;
        } else if (alivePlayers.size() == 1 && allPlayers.size() > 1) {
            // single winner
            game.setFinished(true);
            game.setWinnerInfo(new WinnerInfo(alivePlayers.get(0).getPlayerId(), false));
            return true;
        }

        // Check if maxTurns is reached for all players
        GameConfiguration config = game.getConfig();
        boolean maxTurnsReached = allPlayers.stream()
                .allMatch(p -> p.getTurnsPlayed() >= config.getMaxTurns());

        if (maxTurnsReached) {
            // tie-break logic:
            // 1) The player with more minions left wins.
            // 2) If same # of minions, sum HP.
            // 3) If same HP, compare budgets.
            // 4) Otherwise tie
            String winner = determineWinnerByTiebreak(game);
            if (winner == null) {
                game.setWinnerInfo(new WinnerInfo(null, true)); // tie
            } else {
                game.setWinnerInfo(new WinnerInfo(winner, false));
            }
            game.setFinished(true);
            return true;
        }

        return false;
    }

    private String determineWinnerByTiebreak(Game game) {
        // Gather players
        List<Player> players = new ArrayList<>(game.getPlayers().values());
        players.sort((p1, p2) -> p1.getPlayerId().compareTo(p2.getPlayerId()));

        // Compute (minionCount, sumHP, budget) for each
        // Then compare
        int bestMinionCount = -1;
        double bestHp = -1;
        double bestBudget = -1;
        String bestPlayerId = null;
        boolean tie = false;

        for (Player p : players) {
            int minionCount = (int) p.getMinions().stream().filter(Minion::isAlive).count();
            double sumHp = p.getMinions().stream().filter(Minion::isAlive).mapToDouble(Minion::getHp).sum();
            double budget = p.getBudget();

            if (minionCount > bestMinionCount) {
                bestMinionCount = minionCount;
                bestHp = sumHp;
                bestBudget = budget;
                bestPlayerId = p.getPlayerId();
                tie = false;
            } else if (minionCount == bestMinionCount) {
                if (sumHp > bestHp) {
                    bestHp = sumHp;
                    bestBudget = budget;
                    bestPlayerId = p.getPlayerId();
                    tie = false;
                } else if (sumHp == bestHp) {
                    if (budget > bestBudget) {
                        bestBudget = budget;
                        bestPlayerId = p.getPlayerId();
                        tie = false;
                    } else if (budget == bestBudget) {
                        // tie remains
                        tie = true;
                    }
                }
            }
        }

        if (tie) {
            return null;
        }
        return bestPlayerId;
    }

    private void handleBotTurn(Game game) {
        if (game.isFinished()) return;
        int idx = game.getCurrentPlayerIndex();
        String currentPlayerId = game.getPlayerOrder().get(idx);
        Player bot = game.getPlayers().get(currentPlayerId);

        // Minimal bot logic: just nextTurn again (which will do budget, interest, movement, etc.)
        // In a real scenario, you'd spawn or purchase hex if you want to, then do movement.
        nextTurn(game.getGameId());
    }
}
