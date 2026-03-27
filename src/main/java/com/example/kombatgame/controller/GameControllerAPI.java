package com.example.kombatgame.controller;

import org.springframework.web.bind.annotation.*;
import com.example.kombatgame.service.GameService;
import com.example.kombatgame.service.AutoPlayService;
import com.example.kombatgame.dto.*;

@RestController
@RequestMapping("/game")
public class GameControllerAPI {

    private final GameService service;
    private final AutoPlayService autoPlayService;

    public GameControllerAPI(GameService service, AutoPlayService autoPlayService) {
        this.service = service;
        this.autoPlayService = autoPlayService;
    }

    @PostMapping("/create")
    public GameSessionDTO create(@RequestBody CreateGameRequest req) {
        return service.createGame(req.mode);
    }

    @PostMapping("/join")
    public GameSessionDTO join(@RequestBody JoinRequest req) {
        return service.joinGame(req.gameId);
    }

    @GetMapping("/{id}/state")
    public GameStateDTO state(@PathVariable String id) {
        return service.getState(id);
    }

    @GetMapping("/{id}")
    public GameSessionDTO session(@PathVariable String id) {
        return service.getSession(id);
    }

    @PostMapping("/{id}/next-turn")
    public GameStateDTO nextTurn(@PathVariable String id) {
        return service.nextTurn(id);
    }

    @PostMapping("/{id}/select-types")
    public GameStateDTO selectTypes(@PathVariable String id, @RequestBody SelectTypesRequest req) {
        return service.selectTypes(id, req.types);
    }

    @PostMapping("/{id}/initial-spawn")
    public GameStateDTO initialSpawn(@PathVariable String id, @RequestBody InitialSpawnRequest req) {
        return service.initialSpawn(id, req.typeName, req.row, req.col);
    }

    @PostMapping("/{id}/spawn")
    public GameStateDTO spawn(@PathVariable String id, @RequestBody SpawnRequest req) {
        return service.spawn(id, req.typeName, req.row, req.col);
    }

    @PostMapping("/{id}/purchase")
    public GameStateDTO purchase(@PathVariable String id, @RequestBody PurchaseRequest req) {
        // ส่ง row และ col เข้าไป (ลำดับนี้ต้องตรงกับที่ GameService รับ)
        return service.purchase(id, req.row, req.col);
    }

    @PostMapping("/{id}/surrender")
    public GameStateDTO surrender(@PathVariable String id) {
        return service.surrender(id);
    }

    @PostMapping("/{id}/setup-done")
    public GameStateDTO setupDone(@PathVariable String id) {
        return service.setupDone(id);
    }

    @PostMapping("/{id}/auto-play/start")
    public GameStateDTO startAutoPlay(@PathVariable String id) {
        autoPlayService.startAutoPlay(id);
        return service.getState(id);
    }

    @PostMapping("/{id}/auto-play/stop")
    public GameStateDTO stopAutoPlay(@PathVariable String id) {
        autoPlayService.stopAutoPlay(id);
        return service.getState(id);
    }

    @GetMapping("/{id}/auto-play/status")
    public boolean isAutoPlaying(@PathVariable String id) {
        return autoPlayService.isAutoPlaying(id);
    }

    @GetMapping("/config")
    public GameConfigDTO getConfig() {
        return service.getConfig();
    }

    @GetMapping("/{id}/room-status")
    public RoomStatusDTO roomStatus(@PathVariable String id) {
        return service.getRoomStatus(id);
    }

    @PostMapping("/{id}/submit-config")
    public RoomStatusDTO submitConfig(@PathVariable String id, @RequestBody SelectTypesRequest req) {
        service.submitConfig(id, req.types);
        return service.getRoomStatus(id);
    }

    @PostMapping("/{id}/agree")
    public RoomStatusDTO agree(@PathVariable String id, @RequestParam(defaultValue = "guest") String role) {
        service.agree(id, "host".equalsIgnoreCase(role));
        return service.getRoomStatus(id);
    }

    @PostMapping("/{id}/disagree")
    public RoomStatusDTO disagree(@PathVariable String id) {
        service.disagree(id);
        return service.getRoomStatus(id);
    }
}