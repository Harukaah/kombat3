package com.kombat3.kombat3.service;

import com.kombat3.kombat3.config.GameParamsConfig;
import com.kombat3.kombat3.dto.GameCreationDTO;
import com.kombat3.kombat3.dto.JoinGameDTO;
import com.kombat3.kombat3.model.*;
import com.kombat3.kombat3.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameParamsConfig params;

    public GameService(GameRepository gameRepository, GameParamsConfig params) {
        this.gameRepository = gameRepository;
        this.params = params;
    }

    public synchronized Game createGame(GameCreationDTO dto) {
        String gameId = UUID.randomUUID().toString();
        GameConfiguration gameConfig = new GameConfiguration(
                params.getSpawnCost(),
                params.getHexPurchaseCost(),
                params.getInitBudget(),
                params.getInitHp(),
                params.getTurnBudget(),
                params.getMaxBudget(),
                params.getInterestPct(),
                params.getMaxTurns(),
                params.getMaxSpawns()
        );

        Game game = new Game(gameId, dto.getGameMode(), gameConfig);

        // Create host player
        Player host = new Player(UUID.randomUUID().toString(),
                dto.getHostPlayerName(),
                gameConfig.getInitBudget(),
                null);

        game.getPlayers().put(host.getPlayerId(), host);
        gameRepository.save(game);
        return game;
    }

    public synchronized Game joinGame(JoinGameDTO dto) {
        Game game = gameRepository.findById(dto.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        if (game.getPlayers().size() >= 2 && game.getMode() == GameMode.DUEL) {
            throw new IllegalStateException("Game already has 2 players in DUEL mode");
        }

        // Create second player
        GameConfiguration config = game.getConfig();
        Player newPlayer = new Player(UUID.randomUUID().toString(),
                dto.getPlayerName(),
                config.getInitBudget(),
                null);

        game.getPlayers().put(newPlayer.getPlayerId(), newPlayer);

        gameRepository.save(game);
        return game;
    }

    public Game findGame(String gameId) {
        return gameRepository.findById(gameId);
    }

    public synchronized void startGame(String gameId) {
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        if (game.isStarted()) {
            throw new IllegalStateException("Game already started");
        }

        // Gather players
        List<String> playerIds = new ArrayList<>(game.getPlayers().keySet());
        if (playerIds.isEmpty()) {
            throw new IllegalStateException("No players in game.");
        }
        game.setPlayerOrder(playerIds);

        // Assign initial spawn areas
        if (playerIds.size() > 0) {
            Player p1 = game.getPlayers().get(playerIds.get(0));
            assignInitialSpawnArea(game.getBoard(), p1, true);
        }
        if (playerIds.size() > 1) {
            Player p2 = game.getPlayers().get(playerIds.get(1));
            assignInitialSpawnArea(game.getBoard(), p2, false);
        }

        game.setStarted(true);
        game.setCurrentPlayerIndex(0);
        gameRepository.save(game);
    }

    private void assignInitialSpawnArea(HexCell[][] board, Player player, boolean topLeft) {
        // For demonstration, pick 5 hexes in top-left or bottom-right
        if (topLeft) {
            int assigned = 0;
            for (int r = 0; r < 8 && assigned < 5; r++) {
                for (int c = 0; c < 8 && assigned < 5; c++) {
                    if (r < 2 && c < 3) {
                        player.addSpawnableHex(board[r][c]);
                        assigned++;
                    }
                }
            }
        } else {
            int assigned = 0;
            for (int r = 7; r >= 0 && assigned < 5; r--) {
                for (int c = 7; c >= 0 && assigned < 5; c--) {
                    if (r > 5 && c > 4) {
                        player.addSpawnableHex(board[r][c]);
                        assigned++;
                    }
                }
            }
        }
    }
}
