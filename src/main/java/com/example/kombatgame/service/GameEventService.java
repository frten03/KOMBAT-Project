package com.example.kombatgame.service;

import com.example.kombatgame.dto.GameStateDTO;
import com.example.kombatgame.dto.RoomStatusDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameEventService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameEventService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastGameStateUpdate(String gameId, GameStateDTO dto) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/state", dto);
    }

    public void broadcastGameOver(String gameId, String winner, String reason) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/gameOver", 
            new GameOverEvent(winner, reason));
    }

    public void broadcastTurnChanged(String gameId, int currentPlayer, int turn) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/turn", 
            new TurnChangedEvent(currentPlayer, turn));
    }
    
    public void broadcastRoomStatusUpdate(String gameId, RoomStatusDTO dto) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/room", dto);
    }
    
    public void broadcastConfigSubmitted(String gameId) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/configSubmitted", 
            new ConfigSubmittedEvent());
    }
    
    public void broadcastConfigReset(String gameId) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/configReset", 
            new ConfigResetEvent());
    }
    
    public void broadcastGameStarted(String gameId) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/gameStarted", 
            new GameStartedEvent());
    }

    public void sendToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }

    public record GameOverEvent(String winner, String reason) {}
    public record TurnChangedEvent(int currentPlayer, int turn) {}
    public record ConfigSubmittedEvent() {}
    public record ConfigResetEvent() {}
    public record GameStartedEvent() {}
}
