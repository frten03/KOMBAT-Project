package com.example.kombatgame.service;

import com.example.kombatgame.game.GameState;
import com.example.kombatgame.game.StrategyEvaluator;
import com.example.kombatgame.model.*;
import com.example.kombatgame.repository.GameSessionRepository;
import com.example.kombatgame.dto.*;
import com.example.kombatgame.mapper.GameMapper;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameSessionRepository repo;
    private final StrategyEvaluator evaluator; // ของเธอ

    public GameService(GameSessionRepository repo, StrategyEvaluator  evaluator) {
        this.repo = repo;
        this.evaluator = evaluator;
    }

    // create game
    public GameSessionDTO createGame(String mode) {
        GameSession s = repo.createSession(mode);

        // player 1
        s.addPlayer("P1");

        // auto mode → start ทันที
        if (mode.equals("auto")) {
            s.addPlayer("BOT");
            s.setStarted(true);
        }

        return GameMapper.toSessionDTO(s);
    }

    // join game
    public GameSessionDTO joinGame(String gameId) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        if (s.getMode().equals("duel")) {
            if (s.getPlayers().size() < 2) {
                s.addPlayer("P2");
            }

            // start เมื่อครบ 2 คน
            if (s.getPlayers().size() == 2) {
                s.setStarted(true);
            }
        }

        return GameMapper.toSessionDTO(s);
    }

    public GameStateDTO nextTurn(String gameId) {
        GameSession s = repo.getSession(gameId);

        if (s == null) throw new RuntimeException("Game not found");

        if (!s.isStarted()) {
            throw new RuntimeException("Game not started");
        }

        GameState state = s.getState();

        state.playTurn();

        return GameMapper.toStateDTO(state);
    }

    // spawn
    public GameStateDTO spawn(String gameId, int row, int col, String type) {
        GameSession s = repo.getSession(gameId);

        s.getState().spawnMinion(type, row, col);

        return GameMapper.toStateDTO(s.getState());
    }

    // get state
    public GameStateDTO getState(String gameId) {
        return GameMapper.toStateDTO(repo.getSession(gameId).getState());
    }

    public GameSessionDTO getSession(String gameId) {
        return GameMapper.toSessionDTO(repo.getSession(gameId));
    }
}