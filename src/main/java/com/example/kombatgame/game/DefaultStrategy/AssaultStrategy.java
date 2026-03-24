package com.example.kombatgame.game.DefaultStrategy;

public class AssaultStrategy {
    public static final String SOURCE = """
            t = t + 1
            
            while (1) {
            
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
            
                if (op % 10 - 1) then {} else { if (Budget - 30) then shoot up 30 else move up done }
                if (op % 10 - 2) then {} else { if (Budget - 30) then shoot upright 30 else move upright done }
                if (op % 10 - 3) then {} else { if (Budget - 30) then shoot downright 30 else move downright done }
                if (op % 10 - 4) then {} else { if (Budget - 30) then shoot down 30 else move down done }
                if (op % 10 - 5) then {} else { if (Budget - 30) then shoot downleft 30 else move downleft done }
            
                { if (Budget - 30) then shoot upleft 30 else move upleft done }
              }
            
              else move up done
            }
    """;
}
