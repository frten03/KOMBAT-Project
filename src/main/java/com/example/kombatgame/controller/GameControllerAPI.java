package com.example.kombatgame.controller;

import org.springframework.web.bind.annotation.*;
import com.example.kombatgame.service.GameService;
import com.example.kombatgame.dto.*;

@RestController
@RequestMapping("/game")
public class GameControllerAPI {

    private final GameService service;

    public GameControllerAPI(GameService service) {
        this.service = service;
    }

    // create game
    @PostMapping("/create")
    public GameSessionDTO create(@RequestBody CreateGameRequest req) {
        return service.createGame(req.mode);
    }

    // join game
    @PostMapping("/join")
    public GameSessionDTO join(@RequestBody JoinRequest req) {
        return service.joinGame(req.gameId);
    }

    // get state
    @GetMapping("/{id}/state")
    public GameStateDTO state(@PathVariable String id) {
        return service.getState(id);
    }

    // get session
    @GetMapping("/{id}")
    public GameSessionDTO session(@PathVariable String id) {
        return service.getSession(id);
    }

    // next turn
    @PostMapping("/{id}/next-turn")
    public GameStateDTO nextTurn(@PathVariable String id) {
        return service.nextTurn(id);
    }
}