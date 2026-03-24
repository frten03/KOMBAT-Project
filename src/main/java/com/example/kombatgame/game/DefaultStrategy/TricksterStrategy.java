package com.example.kombatgame.game.DefaultStrategy;

public class TricksterStrategy {
    public static final String SOURCE = """
            t = t + 1
            
            while (1) {
            
              op = opponent
              r = random % 6
            
              if (op)
              then {
            
                if (op % 10 - 1) then {} else { if (Budget - 10) then shoot up 10 else move up done }
                if (op % 10 - 2) then {} else { if (Budget - 10) then shoot upright 10 else move upright done }
                if (op % 10 - 3) then {} else { if (Budget - 10) then shoot downright 10 else move downright done }
                if (op % 10 - 4) then {} else { if (Budget - 10) then shoot down 10 else move down done }
                if (op % 10 - 5) then {} else { if (Budget - 10) then shoot downleft 10 else move downleft done }
            
                { if (Budget - 10) then shoot upleft 10 else move upleft done }
              }
            
              else {
            
                if (r - 1) then {} else { move up done }
                if (r - 2) then {} else { move upright done }
                if (r - 3) then {} else { move downright done }
                if (r - 4) then {} else { move down done }
                if (r - 5) then {} else { move downleft done }
            
                move upleft done
              }
            }
    """;
}
