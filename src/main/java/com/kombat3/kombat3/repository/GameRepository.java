package com.kombat3.kombat3.repository;

import com.kombat3.kombat3.model.Game;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepository {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    public Game save(Game game) {
        games.put(game.getGameId(), game);
        return game;
    }

    public Game findById(String gameId) {
        return games.get(gameId);
    }

    public boolean existsById(String gameId) {
        return games.containsKey(gameId);
    }
}
