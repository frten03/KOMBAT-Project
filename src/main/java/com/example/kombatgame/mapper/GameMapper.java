package com.example.kombatgame.mapper;

import com.example.kombatgame.game.GameState;
import com.example.kombatgame.game.Minion;
import com.example.kombatgame.model.*;
import com.example.kombatgame.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameMapper {

    public static GameSessionDTO toSessionDTO(GameSession s) {
        GameSessionDTO dto = new GameSessionDTO();
        dto.gameId = s.getGameId();
        dto.mode = s.getMode();
        dto.started = s.isStarted();
        dto.playerCount = s.getPlayers().size();
        return dto;
    }

    public static GameStateDTO toStateDTO(GameState state) {
        GameStateDTO dto = new GameStateDTO();

        // current player
        dto.currentPlayer = state.getCurrentPlayer().getId();

        // budget ของ player ปัจจุบัน
        dto.budget = state.getCurrentPlayer().getBudget();

        // รวม minions ของทั้งสองฝั่ง
        List<Minion> allMinions = new ArrayList<>();
        allMinions.addAll(state.getPlayer1().getMinions());
        allMinions.addAll(state.getPlayer2().getMinions());

        dto.minions = allMinions.stream()
                .map(m -> {
                    MinionDTO d = new MinionDTO();
                    d.row = m.getRow();
                    d.col = m.getCol();
                    d.hp = m.getHp();
                    d.owner = m.getOwner().getId();
                    return d;
                })
                .collect(Collectors.toList());

        return dto;
    }
}