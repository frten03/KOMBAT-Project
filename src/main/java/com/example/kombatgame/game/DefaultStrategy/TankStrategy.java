package com.example.kombatgame.game.DefaultStrategy;

public class TankStrategy {
    public static final String SOURCE = """
        t = t + 1
        r = 0
        
        while (3 - r) {
        
          op = opponent
        
          if (op / 10 - 1)
          then {
        
            if (op % 10 - 1) then {} else { move up done }
            if (op % 10 - 2) then {} else { move upright done }
            if (op % 10 - 3) then {} else { move downright done }
            if (op % 10 - 4) then {} else { move down done }
            if (op % 10 - 5) then {} else { move downleft done }
            move upleft done
          }
        
          else if (op)
          then {
        
            if (op % 10 - 1) then {} else { if (Budget - 15) then shoot up 15 else move up done }
            if (op % 10 - 2) then {} else { if (Budget - 15) then shoot upright 15 else move upright done }
            if (op % 10 - 3) then {} else { if (Budget - 15) then shoot downright 15 else move downright done }
            if (op % 10 - 4) then {} else { if (Budget - 15) then shoot down 15 else move down done }
            if (op % 10 - 5) then {} else { if (Budget - 15) then shoot downleft 15 else move downleft done }
        
            { if (Budget - 15) then shoot upleft 15 else move upleft done }
          }
        
          else {
            r = r + 1
          }
        }
    """;
}
