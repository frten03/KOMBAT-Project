package com.example.kombatgame.dto;

import java.util.ArrayList;
import java.util.List;

public class TurnLogsDTO {
    public List<String> logs = new ArrayList<>();

    public void addLog(String log) {
        logs.add(log);
    }

    public void addSeparator() {
        logs.add("-----------------------");
    }

    public List<String> getLogs() {
        return logs;
    }
}
