package com.example.kombatgame.repository;

import com.example.kombatgame.game.ConfigLoader;
import com.example.kombatgame.game.GameConfig;
import com.example.kombatgame.game.GameMode;
import com.example.kombatgame.game.GameState;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.kombatgame.model.GameSession;

@Repository
public class GameSessionRepository {

    private final Map<String, GameSession> sessions = new HashMap<>();

    public GameSession createSession(String mode) {

        String id = UUID.randomUUID().toString().substring(0, 6);

        // กัน mode พิมพ์ผิด
        GameMode gameMode;
        try {
            gameMode = GameMode.valueOf(mode.toUpperCase());
        } catch (Exception e) {
            gameMode = GameMode.DUEL; // default
        }

        try {
            // โหลด config
            GameConfig config = ConfigLoader.load("config.txt");

            // สร้าง GameState
            GameState state = new GameState(config, gameMode);

            GameSession session = new GameSession(id, gameMode, state);

            sessions.put(id, session);
            return session;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    public GameSession getSession(String id) {
        return sessions.get(id);
    }
}