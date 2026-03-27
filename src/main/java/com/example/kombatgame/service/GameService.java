package com.example.kombatgame.service;

import com.example.kombatgame.game.GameState;
import com.example.kombatgame.game.StrategyEvaluator;
import com.example.kombatgame.game.MinionType;
import com.example.kombatgame.model.*;
import com.example.kombatgame.repository.GameSessionRepository;
import com.example.kombatgame.dto.*;
import com.example.kombatgame.mapper.GameMapper;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameSessionRepository repo;
    private final StrategyEvaluator evaluator;
    private final GameEventService eventService;

    public GameService(GameSessionRepository repo, StrategyEvaluator evaluator, GameEventService eventService) {
        this.repo = repo;
        this.evaluator = evaluator;
        this.eventService = eventService;
    }

    private void broadcastState(String gameId, GameStateDTO dto) {
        eventService.broadcastGameStateUpdate(gameId, dto);
    }

    private void broadcastGameOver(String gameId, GameState state) {
        String winner = "Unknown";
        if (state.determineWinner() != null) {
            winner = "Player " + state.determineWinner().getId();
        }
        eventService.broadcastGameOver(gameId, winner, state.getGameOverReason());
    }

    private void broadcastTurnChanged(String gameId, GameState state) {
        eventService.broadcastTurnChanged(gameId, state.getCurrentPlayer().getId(), state.getGlobalTurn());
    }

    private void broadcastConfigSubmitted(String gameId) {
        eventService.broadcastConfigSubmitted(gameId);
    }

    private void broadcastConfigReset(String gameId) {
        eventService.broadcastConfigReset(gameId);
    }

    private void broadcastGameStarted(String gameId) {
        eventService.broadcastGameStarted(gameId);
    }

    private void broadcastRoomStatusUpdate(String gameId, RoomStatusDTO dto) {
        eventService.broadcastRoomStatusUpdate(gameId, dto);
    }

    public GameSessionDTO createGame(String mode) {
        GameSession s = repo.createSession(mode);
        s.addPlayer("P1");

        if (mode.equals("auto") || mode.equals("solitaire")) {
            s.addPlayer("BOT");
            s.setStarted(true);
        }

        return GameMapper.toSessionDTO(s);
    }

    public GameSessionDTO joinGame(String gameId) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        System.out.println("[JoinGame] gameId=" + gameId + " mode=" + s.getMode() + " players=" + s.getPlayers().size());

        if ("DUEL".equalsIgnoreCase(s.getMode())) {
            if (s.getPlayers().size() < 2) {
                s.addPlayer("P2");
                System.out.println("[JoinGame] Added P2, now players=" + s.getPlayers().size());
            }

            if (s.getPlayers().size() == 2) {
                s.setStarted(true);
                System.out.println("[JoinGame] Both joined, setStarted=true");
            }
        }

        return GameMapper.toSessionDTO(s);
    }

    public GameStateDTO nextTurn(String gameId) {
        System.out.println("[DEBUG] GameService.nextTurn() called");
        GameSession s = repo.getSession(gameId);

        if (s == null) {
            System.out.println("[DEBUG] Game not found: " + gameId);
            throw new RuntimeException("Game not found");
        }
        if (!s.isStarted()) {
            System.out.println("[DEBUG] Game not started");
            throw new RuntimeException("Game not started");
        }

        GameState state = s.getState();

        // Check if both players are bots (AUTO mode)
        boolean isAutoMode = state.getPlayer1().isBot() && state.getPlayer2().isBot();

        if (isAutoMode) {
            // In AUTO mode: play one full round (both players)
            System.out.println("[DEBUG] AUTO mode: playing one full round");
            state.playTurn();  // Player 1
            if (!state.isGameOver()) {
                state.playTurn();  // Player 2
            }
        } else {
            // In SOLITAIRE/DUEL: play human's turn, then auto-play bot turns
            System.out.println("[DEBUG] Calling playTurn() for human");
            state.playTurn();

            // Auto-execute bot turns until it's human's turn or game over
            while (!state.isGameOver() && state.getCurrentPlayer().isBot()) {
                System.out.println("[DEBUG] Auto-playing bot turn");
                state.playTurn();
            }
        }

        System.out.println("[DEBUG] Returning state, phase: " + state.getPhase());
        GameStateDTO dto = GameMapper.toStateDTO(state);

        // Broadcast events
        if (state.isGameOver()) {
            broadcastGameOver(gameId, state);
        } else {
            broadcastState(gameId, dto);
            broadcastTurnChanged(gameId, state);
        }

        return dto;
    }

    public GameStateDTO selectTypes(String gameId, List<String> types) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();
        state.syncMinionTypesFromHost(types);

        GameStateDTO dto = GameMapper.toStateDTO(state);
        broadcastState(gameId, dto);
        return dto;
    }

    public GameStateDTO initialSpawn(String gameId, String typeName, int row, int col) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();
        String errorReason = state.getInitialSpawnError(typeName, col, row);

        if (errorReason != null) {
            throw new RuntimeException(errorReason);
        }

        boolean success = state.initialSpawn(typeName, col, row);

        if (!success) {
            throw new RuntimeException("Initial spawn failed");
        }

        GameStateDTO dto = GameMapper.toStateDTO(state);
        broadcastState(gameId, dto);
        broadcastTurnChanged(gameId, state);
        return dto;
    }

    public GameStateDTO spawn(String gameId, String typeName, int row, int col) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();
        boolean success = state.spawnMinion(typeName, col, row);

        if (!success) {
            throw new RuntimeException("Spawn failed");
        }

        GameStateDTO dto = GameMapper.toStateDTO(state);
        broadcastState(gameId, dto);
        return dto;
    }

    public GameStateDTO purchase(String gameId, int row, int col) {
        GameSession s = repo.getSession(gameId);
        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();

        boolean success = state.purchaseHex(col, row);

        if (!success) {
            String error = state.getPurchaseError(col, row);
            throw new RuntimeException(error);
        }

        GameStateDTO dto = GameMapper.toStateDTO(state);
        broadcastState(gameId, dto);
        return dto;
    }

    public GameStateDTO surrender(String gameId) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();
        state.surrender();

        GameStateDTO dto = GameMapper.toStateDTO(state);
        if (state.isGameOver()) {
            broadcastGameOver(gameId, state);
        } else {
            broadcastState(gameId, dto);
        }
        return dto;
    }

    public GameStateDTO setupDone(String gameId) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        GameState state = s.getState();
        state.lockSetupPhase();

        GameStateDTO dto = GameMapper.toStateDTO(state);
        broadcastState(gameId, dto);
        return dto;
    }

    public GameStateDTO getState(String gameId) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }
        return GameMapper.toStateDTO(s.getState());
    }

    public GameSessionDTO getSession(String gameId) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }
        return GameMapper.toSessionDTO(s);
    }

    public GameConfigDTO getConfig() {
        try {
            var config = com.example.kombatgame.game.ConfigLoader.load("config.txt");
            return new GameConfigDTO(
                    config.getSpawnCost(),
                    config.getHexPurchaseCost(),
                    config.getInitBudget(),
                    config.getInitHp(),
                    config.getTurnBudget(),
                    config.getMaxBudget(),
                    config.getInterestPct(),
                    config.getMaxTurns(),
                    config.getMaxSpawns()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    public RoomStatusDTO getRoomStatus(String gameId) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }

        RoomStatusDTO dto = new RoomStatusDTO();
        dto.guestJoined = s.hasGuestJoined();
        dto.configSubmitted = s.isHostConfigSubmitted();
        dto.hostAgreed = s.isHostAgreed();
        dto.guestAgreed = s.isGuestAgreed();
        dto.hostConfig = s.getHostMinionConfig();

        System.out.println("[RoomStatus] gameId=" + gameId + " guestJoined=" + dto.guestJoined +
                " configSubmitted=" + dto.configSubmitted + " hostAgreed=" + dto.hostAgreed + " guestAgreed=" + dto.guestAgreed +
                " players=" + s.getPlayers().size());

        return dto;
    }

    public void submitConfig(String gameId, List<String> typeNames) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }

        System.out.println("[submitConfig] Received typeNames: " + typeNames);

        GameState state = s.getState();
        System.out.println("[submitConfig] Base types: " +
                state.getBaseTypes().stream().map(t -> t.getName()).toList());

        List<MinionTypeDTO> hostConfig = new ArrayList<>();

        for (String name : typeNames) {
            MinionType type = state.getBaseTypes().stream()
                    .filter(t -> t.getName().equals(name))
                    .findFirst()
                    .orElse(null);

            if (type != null) {
                MinionTypeDTO dto = new MinionTypeDTO();
                dto.name = type.getName();
                dto.defense = type.getDefense();
                dto.strategy = type.getStrategySource();
                hostConfig.add(dto);
            } else {
                System.out.println("[submitConfig] WARNING: type '" + name + "' not found in base types");
            }
        }

        System.out.println("[submitConfig] Host config: " + hostConfig.stream().map(dto -> dto.name).toList());

        s.setHostMinionConfig(hostConfig);
        s.setHostConfigSubmitted(true);
        s.setHostAgreed(false);
        s.setGuestAgreed(false);

        broadcastConfigSubmitted(gameId);
    }

    public void agree(String gameId, boolean isHost) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }

        if ("DUEL".equalsIgnoreCase(s.getMode())) {
            if (isHost) {
                s.setHostAgreed(true);
            } else {
                s.setGuestAgreed(true);
            }

            if (s.isHostConfigSubmitted() && s.isHostAgreed() && s.isGuestAgreed()) {
                s.setStarted(true);

                GameState state = s.getState();
                List<String> typeNames = s.getHostMinionConfig().stream()
                        .map(dto -> dto.name)
                        .toList();
                System.out.println("[Agree] Syncing types: " + typeNames);
                state.syncMinionTypesFromHost(typeNames);

                System.out.println("[Agree] Player1 types after sync: " +
                        state.getPlayer1().getMinionTypes().stream().map(t -> t.getName()).toList());
                System.out.println("[Agree] Player2 types after sync: " +
                        state.getPlayer2().getMinionTypes().stream().map(t -> t.getName()).toList());
                broadcastState(gameId, GameMapper.toStateDTO(state));
                broadcastGameStarted(gameId);
            }

            broadcastRoomStatusUpdate(gameId, getRoomStatus(gameId));
        }
    }

    public void disagree(String gameId) {
        GameSession s = repo.getSession(gameId);
        if (s == null) {
            throw new RuntimeException("Game not found: " + gameId);
        }

        if (s.getMode().equals("DUEL")) {
            s.resetConfig();
            broadcastConfigReset(gameId);
        }
    }
}