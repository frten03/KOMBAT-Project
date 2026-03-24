package com.example.kombatgame.game.DefaultStrategy;

public class ScoutStrategy {
    public static final String SOURCE = """
            m = 0
            
            while (3 - m) {
            
              op = opponent
            
              if (op)
              then {
            
                if (op % 10 - 1) then {} else { move up done }
                if (op % 10 - 2) then {} else { move upright done }
                if (op % 10 - 3) then {} else { move downright done }
                if (op % 10 - 4) then {} else { move down done }
                if (op % 10 - 5) then {} else { move downleft done }
                move upleft done
              }
            
              else {
            
                try = 0
            
                while (3 - try) {
            
                  dir = random % 6
            
                  if (dir - 1) then {} else { if ((nearby up % 10 + 1) ^ 2) then { move up done } else{} }
                  if (dir - 2) then {} else { if ((nearby upright % 10 + 1) ^ 2) then { move upright done } else{} }
                  if (dir - 3) then {} else { if ((nearby downright % 10 + 1) ^ 2) then { move downright done } else{} }
                  if (dir - 4) then {} else { if ((nearby down % 10 + 1) ^ 2) then { move down done } else{} }
                  if (dir - 5) then {} else { if ((nearby downleft % 10 + 1) ^ 2) then { move downleft done } else{} }
            
                  if ((nearby upleft % 10 + 1) ^ 2) then { move upleft done } else{}
            
                  try = try + 1
                }
            
                m = m + 1
              }
            }
    """;
}
