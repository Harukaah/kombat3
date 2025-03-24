package com.kombat3.kombat3.controller;

import com.kombat3.kombat3.dto.JoinGameDTO;
import com.kombat3.kombat3.dto.SpawnRequestDTO;
import com.kombat3.kombat3.model.Game;
import com.kombat3.kombat3.model.Player;
import com.kombat3.kombat3.service.GameService;
import com.kombat3.kombat3.service.PlayerService;
import com.kombat3.kombat3.service.TurnService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final TurnService turnService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(GameService gameService, PlayerService playerService, TurnService turnService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.turnService = turnService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/join")
    public void joinGame(JoinGameDTO dto) {
        Game game = gameService.joinGame(dto);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
    }

    @MessageMapping("/game/spawn")
    public void spawnMinion(SpawnRequestDTO dto) {
        Game game = gameService.findGame(dto.getGameId());
        if (game != null) {
            Player player = game.getPlayers().get(dto.getPlayerId());
            if (player == null) {
                throw new IllegalArgumentException("Player not found in this game.");
            }
            playerService.spawnMinion(game, player, dto.getMinionName(), dto.getDefFactor(), dto.getRow(), dto.getCol());
            messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
        }
    }

    @MessageMapping("/game/nextTurn/{gameId}")
    public void nextTurn(@DestinationVariable String gameId) {
        Game game = turnService.nextTurn(gameId);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
    }
}
