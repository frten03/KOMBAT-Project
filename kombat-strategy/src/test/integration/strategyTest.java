package test.integration;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import test.unit.testutil.RecordingDummyGameAPI;

import java.util.HashMap;
import java.util.Map;

public class strategyTest {
    private EvalContext runOneTurn(RecordingDummyGameAPI api, String src, Map<String, Long> global) {
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStrategy(); // ตอนนี้ยังใช้ตัวนี้ไปก่อน

        EvalContext ctx = new EvalContext(api, global);
        s.eval(ctx);

        return ctx;
    }

    @Test
    void simulateMultipleTurns() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        Map<String, Long> global = new HashMap<>();

        String bot = """
        t = t + 1
        op = opponent
 
        if (nearby up) then
            if (Budget - 4) then shoot up 4 else move down
        else
        if (nearby down) then
            if (Budget - 4) then shoot down 4 else move up
        else
        if (nearby upleft) then
            if (Budget - 4) then shoot upleft 4 else move down
        else
        if (nearby upright) then
            if (Budget - 4) then shoot upright 4 else move down
        else
        if (nearby downleft) then
            if (Budget - 4) then shoot downleft 4 else move up
        else
        if (nearby downright) then
            if (Budget - 4) then shoot downright 4 else move up
        else
        
        if (op) then
            if (op / 10 - 1) then
                if (t % 2) then move up else move upright
            else
                if (Budget - 6) then shoot up 6 else move down
        else
        
        if (t % 3) then
            move up
        else
            if (t % 2) then move upleft else move upright
        """;

        for (int turn = 1; turn <= 10; turn++) {

            // 🔧 ปรับ environment ต่อ turn
            if (turn == 1) api.setNearby(0);
            if (turn == 2) api.setNearby(1);      // เจอศัตรูใกล้
            if (turn == 5) api.setOpponent(25);   // ศัตรูไกล
            if (turn == 7) api.setBudget(3);      // เงินน้อย

            EvalContext ctx = runOneTurn(api, bot, global);

            System.out.println("Turn " + turn);
            System.out.println("moveCount = " + api.moveCount);
            System.out.println("shootCount = " + api.shootCount);
            System.out.println("-------------------");
        }
    }
}
