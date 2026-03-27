package com.example.kombatgame.mapper;

import com.example.kombatgame.game.GameState;
import com.example.kombatgame.game.Minion;
import com.example.kombatgame.model.*;
import com.example.kombatgame.dto.*;


import java.util.ArrayList;
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

        dto.phase = state.getPhase().name();
        dto.currentPlayer = state.getCurrentPlayer().getId();
        dto.globalTurn = state.getGlobalTurn();
        dto.budget = state.getCurrentPlayer().getBudget();
        dto.spawnsLeft = state.getCurrentPlayer().getSpawnsLeft();
        dto.spawnCost = state.getConfig().getSpawnCost();
        dto.hexPurchaseCost = state.getConfig().getHexPurchaseCost();
        dto.gameOver = state.getPhase() == GameState.Phase.FINISHED;
        dto.gameOverReason = state.getGameOverReason();

        dto.player1SpawnableHexes = new ArrayList<>(state.getPlayer1().getSpawnableHexes());
        dto.player2SpawnableHexes = new ArrayList<>(state.getPlayer2().getSpawnableHexes());

        dto.player1Minions = state.getPlayer1().getMinions().stream()
                .map(GameMapper::toMinionDTO)
                .collect(Collectors.toList());

        dto.player2Minions = state.getPlayer2().getMinions().stream()
                .map(GameMapper::toMinionDTO)
                .collect(Collectors.toList());

        dto.availableMinionTypes = state.getBaseTypes().stream()
                .map(GameMapper::toMinionTypeDTO)
                .collect(Collectors.toList());

        dto.turnLogs = state.getTurnLogs().getLogs();

        dto.player1MinionTypes = state.getPlayer1().getMinionTypes().stream()
                .map(GameMapper::toMinionTypeDTO)
                .collect(Collectors.toList());

        dto.player2MinionTypes = state.getPlayer2().getMinionTypes().stream()
                .map(GameMapper::toMinionTypeDTO)
                .collect(Collectors.toList());
        return dto;
    }

    private static MinionTypeDTO toMinionTypeDTO(com.example.kombatgame.game.MinionType t) {
        MinionTypeDTO mt = new MinionTypeDTO();
        mt.name = t.getName();
        mt.defense = t.getDefense();
        mt.strategy = t.getStrategySource();
        return mt;
    }

    public static MinionDTO toMinionDTO(Minion m) {
        MinionDTO d = new MinionDTO();
        d.id = m.getId();
        d.row = m.getRow();
        d.col = m.getCol();
        d.hp = m.getHp();
        d.maxHp = m.getMaxHp();
        d.defense = m.getDefense();
        d.typeName = m.getType() != null ? m.getType().getName() : "Unknown";
        d.owner = m.getOwner().getId();
        return d;
    }
}