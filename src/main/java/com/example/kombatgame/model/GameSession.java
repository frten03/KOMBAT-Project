package com.example.kombatgame.model;

import com.example.kombatgame.game.GameMode;
import com.example.kombatgame.game.GameState;
import com.example.kombatgame.dto.MinionTypeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class GameSession {
    @Getter
    private String gameId;
    private GameMode mode;
    @Setter
    @Getter
    private boolean started = false;
    @Getter
    private List<String> players = new ArrayList<>();
    @Getter
    private GameState state;

    @Setter
    @Getter
    private boolean hostConfigSubmitted = false;
    @Getter
    @Setter
    private boolean hostAgreed = false;
    @Getter
    @Setter
    private boolean guestAgreed = false;
    @Getter
    @Setter
    private List<MinionTypeDTO> hostMinionConfig = new ArrayList<>();

    public GameSession(String gameId, GameMode mode, GameState state) {
        this.gameId = gameId;
        this.mode = mode;
        this.state = state;
    }

    public String getMode() { return mode.name(); }

    public boolean hasGuestJoined() { return players.size() >= 2; }

    public void resetConfig() {
        this.hostConfigSubmitted = false;
        this.hostAgreed = false;
        this.guestAgreed = false;
        this.hostMinionConfig.clear();
    }

    public void addPlayer(String playerId) {
        players.add(playerId);
    }
}