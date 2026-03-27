package com.example.kombatgame.service;

import com.example.kombatgame.game.GameState;
import com.example.kombatgame.game.Player;
import com.example.kombatgame.model.GameSession;
import com.example.kombatgame.repository.GameSessionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoPlayService {

    private final GameSessionRepository repo;
    private final ConcurrentHashMap<String, Boolean> autoPlayRunning = new ConcurrentHashMap<>();

    public AutoPlayService(GameSessionRepository repo) {
        this.repo = repo;
    }

    public boolean isAutoPlaying(String gameId) {
        return autoPlayRunning.getOrDefault(gameId, false);
    }

    @Async
    public CompletableFuture<Boolean> startAutoPlay(String gameId) {
        autoPlayRunning.put(gameId, true);
        
        try {
            while (autoPlayRunning.getOrDefault(gameId, false)) {
                GameSession session = repo.getSession(gameId);
                if (session == null) break;
                
                GameState state = session.getState();
                
                if (state.getPhase() == GameState.Phase.FINISHED) {
                    break;
                }

                if (state.getPhase() == GameState.Phase.SETUP) {
                    if (handleSetupPhase(state)) {
                        Thread.sleep(1000);
                        continue;
                    }
                }

                state.playTurn();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            autoPlayRunning.remove(gameId);
        }
        
        return CompletableFuture.completedFuture(true);
    }

    public void stopAutoPlay(String gameId) {
        autoPlayRunning.put(gameId, false);
    }

    private boolean handleSetupPhase(GameState state) throws InterruptedException {
        if (state.getPlayer1().isBot() && !hasSpawnedInitial(state.getPlayer1())) {
            botInitialSpawn(state, state.getPlayer1());
            Thread.sleep(1000);
            return true;
        }
        
        if (state.getPlayer2().isBot() && !hasSpawnedInitial(state.getPlayer2())) {
            botInitialSpawn(state, state.getPlayer2());
            Thread.sleep(1000);
            return true;
        }
        
        return false;
    }

    private boolean hasSpawnedInitial(Player player) {
        return !player.getMinions().isEmpty();
    }

    private void botInitialSpawn(GameState state, Player player) {
        var spawnableHexes = player.getSpawnableHexes();
        if (spawnableHexes.isEmpty()) {
            System.out.println("[botInitialSpawn] No spawnable hexes for player " + player.getId());
            return;
        }

        var types = player.getMinionTypes();
        if (types.isEmpty()) {
            System.out.println("[botInitialSpawn] No minion types for player " + player.getId());
            return;
        }

        var type = types.get(0);

        String hexCoord = spawnableHexes.iterator().next();
        String[] parts = hexCoord.split(",");
        int c = Integer.parseInt(parts[0]);
        int r = Integer.parseInt(parts[1]);

        System.out.println("[botInitialSpawn] Spawning " + type.getName() + " at (" + c + "," + r + ") for player " + player.getId());
        state.initialSpawnForPlayer(type.getName(), c, r, player);
    }
}
