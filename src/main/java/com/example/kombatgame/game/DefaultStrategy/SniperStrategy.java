package com.example.kombatgame.game.DefaultStrategy;

public class SniperStrategy {
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
        
            if (op % 10 - 1) then {} else {
              cost = 10 ^ (nearby up % 100 + 1)
              if (Budget - cost) then shoot up cost else shoot up 10
              done
            }
        
            if (op % 10 - 2) then {} else {
              cost = 10 ^ (nearby upright % 100 + 1)
              if (Budget - cost) then shoot upright cost else shoot upright 10
              done
            }
        
            if (op % 10 - 3) then {} else {
              cost = 10 ^ (nearby downright % 100 + 1)
              if (Budget - cost) then shoot downright cost else shoot downright 10
              done
            }
        
            if (op % 10 - 4) then {} else {
              cost = 10 ^ (nearby down % 100 + 1)
              if (Budget - cost) then shoot down cost else shoot down 10
              done
            }
        
            if (op % 10 - 5) then {} else {
              cost = 10 ^ (nearby downleft % 100 + 1)
              if (Budget - cost) then shoot downleft cost else shoot downleft 10
              done
            }
        
            {
              cost = 10 ^ (nearby upleft % 100 + 1)
              if (Budget - cost) then shoot upleft cost else shoot upleft 10
              done
            }
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
        
            r = r + 1
          }
        }
    """;
}