package com.kombat3.kombat3.service;

import com.kombat3.kombat3.model.Game;
import com.kombat3.kombat3.model.Player;
import org.springframework.stereotype.Service;

@Service
public class BotService {
    /**
     * In a real scenario, you'd add logic for a bot to spawn minions,
     * purchase hexes, etc., before the movement phase. This is just a placeholder.
     */
    public void executeBotActions(Game game, Player bot) {
        // Example: check if budget is enough to spawn a minion, then spawn randomly
        // or purchase a hex. For now, no-op.
    }
}
