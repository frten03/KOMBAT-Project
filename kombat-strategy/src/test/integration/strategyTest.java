package test.integration;

import org.junit.jupiter.api.Test;
import strategy.ast.Direction;
import strategy.ast.core.Statement;
import strategy.eval.ActionLog;
import strategy.eval.EvalContext;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import test.unit.testutil.RecordGameAPI;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class strategyTest {
    private EvalContext runOneTurn(RecordGameAPI api, String src, Map<String, Long> global) {
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStrategy(); 

        EvalContext ctx = new EvalContext(api, global);
        s.eval(ctx);

        return ctx;
    }

    @Test
    void testSniper() {
        RecordGameAPI api = new RecordGameAPI();
        Map<String, Long> global = new HashMap<>();

        String src = """
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
        
                cost = 10 ^ (nearby upleft % 100 + 1)
                if (Budget - cost) then shoot upleft cost else shoot upleft 10
                done
              }
        
              else {
                r = r + 1
              }
            }
            """;

        // ===== setup =====
        api.setBudget(200);
        api.setOpponent(11);

        api.setNearby(Direction.UP, 311);

        // ===== run =====
        EvalContext ctx = runOneTurn(api, src, global);

        // ===== assert =====
        assertNotNull(ctx);
        assertTrue(api.getLogs().size() > 0);

        ActionLog log = api.getLogs().get(0);

        assertEquals(ActionLog.Type.SHOOT, log.getType());
        assertEquals(Direction.UP, log.getDirection());
        assertTrue(log.getValue() > 0);
    }
}
