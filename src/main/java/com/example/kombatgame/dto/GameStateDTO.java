package com.example.kombatgame.dto;

import java.util.List;

public class GameStateDTO {
    public Integer winner;
    public String phase; // SETUP, RUNNING, FINISHED
    public int currentPlayer;
    public int globalTurn;
    public double budget;
    public long spawnsLeft;
    public List<MinionDTO> player1Minions;
    public List<MinionDTO> player2Minions;
    public List<String> player1SpawnableHexes;
    public List<String> player2SpawnableHexes;
    public List<MinionTypeDTO> availableMinionTypes;
    public long spawnCost;
    public long hexPurchaseCost;
    public String gameOverReason;
    public boolean gameOver;
    public List<String> turnLogs;
    public List<MinionTypeDTO> player1MinionTypes; // เพิ่มอันนี้
    public List<MinionTypeDTO> player2MinionTypes; // เพิ่มอันนี้

}