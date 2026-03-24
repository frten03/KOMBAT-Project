package com.example.kombatgame.model;

import com.example.kombatgame.game.GameMode;
import com.example.kombatgame.game.GameState;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class GameSession {
        @Getter
        private String gameId;
        private GameMode mode; // duel / auto / solitaire

        @Setter
        @Getter
        private boolean started = false;

        @Getter
        private List<String> players = new ArrayList<>();

        @Getter
        private GameState state;

    public GameSession(String gameId, GameMode mode, GameState state) {
        this.gameId = gameId;
        this.mode = mode;
        this.state = state;
    }

    public String getMode() { return mode.name();  }

    public void addPlayer(String playerId) {
        players.add(playerId);
    }
}