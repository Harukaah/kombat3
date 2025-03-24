package com.kombat3.kombat3.controller;

import com.kombat3.kombat3.dto.GameCreationDTO;
import com.kombat3.kombat3.dto.JoinGameDTO;
import com.kombat3.kombat3.dto.SpawnRequestDTO;
import com.kombat3.kombat3.model.Game;
import com.kombat3.kombat3.model.Player;
import com.kombat3.kombat3.service.GameService;
import com.kombat3.kombat3.service.PlayerService;
import com.kombat3.kombat3.service.TurnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/src")
public class GameController {
    @Autowired
    private final GameService gameService;
    @Autowired
    private final PlayerService playerService;
    @Autowired
    private final TurnService turnService;

    public GameController(GameService gameService, PlayerService playerService, TurnService turnService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.turnService = turnService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from KOMBAT!";
    }

    @PostMapping("/create")
    public Game createGame(@RequestBody GameCreationDTO dto) {
        return gameService.createGame(dto);
    }

    @PostMapping("/join")
    public Game joinGame(@RequestBody JoinGameDTO dto) {
        return gameService.joinGame(dto);
    }

    @PostMapping("/start/{gameId}")
    public Game startGame(@PathVariable String gameId) {
        gameService.startGame(gameId);
        return gameService.findGame(gameId);
    }

    @PostMapping("/spawn")
    public Game spawnMinion(@RequestBody SpawnRequestDTO dto) {
        Game game = gameService.findGame(dto.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        Player player = game.getPlayers().get(dto.getPlayerId());
        if (player == null) {
            throw new IllegalArgumentException("Player not found in this game.");
        }
        playerService.spawnMinion(game, player, dto.getMinionName(), dto.getDefFactor(), dto.getRow(), dto.getCol());
        return game;
    }

    @PostMapping("/purchaseHex")
    public Game purchaseHex(@RequestParam String gameId,
                            @RequestParam String playerId,
                            @RequestParam int row,
                            @RequestParam int col) {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        Player player = game.getPlayers().get(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in this game.");
        }

        playerService.purchaseHex(game, player, row, col);
        return game;
    }

    @PostMapping("/nextTurn/{gameId}")
    public Game nextTurn(@PathVariable String gameId) {
        return turnService.nextTurn(gameId);
    }

    @GetMapping("/state/{gameId}")
    public Game getGameState(@PathVariable String gameId) {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        return game;
    }
}
