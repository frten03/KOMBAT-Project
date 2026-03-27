package test.unit.eval;

import org.junit.jupiter.api.Test;
import strategy.ast.Direction;
import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.eval.ActionLog;
import strategy.eval.EvalContext;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import test.unit.testutil.RecordGameAPI;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    private EvalContext run(String src) {
        RecordGameAPI api = new RecordGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, src);
        return ctx;
    }

    private void runWith(EvalContext ctx, String src) {
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStrategy();
        s.eval(ctx);
    }

    //Basic execution

    @Test
    void testMoveExecutes() {
        EvalContext ctx = run("move up");
        assertFalse(ctx.isDone());
    }

    @Test
    void testMoveBudgetInsufficientTerminates() {
        RecordGameAPI api = new RecordGameAPI();
        api.setBudget(0);

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, "move up");

        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.BUDGET_INSUFFICIENT,
                ctx.getTerminationReason());
    }

    @Test
    void testShootExecutes() {
        EvalContext ctx = run("shoot down 10");
        assertFalse(ctx.isDone());
    }

    @Test
    void testShootBudgetInsufficientNoTerminate() {
        RecordGameAPI api = new RecordGameAPI();
        api.setBudget(5);

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, "shoot up 5");

        assertFalse(ctx.isDone());
        assertEquals(0, api.shootCount); // ✔ ใช้ counter
    }

    @Test
    void testDoneStops() {
        EvalContext ctx = run("done");
        assertTrue(ctx.isDone());
    }

    @Test
    void testDoneStopsFurtherExecution() {
        RecordGameAPI api = new RecordGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "done move up");

        assertEquals(0, api.moveCount);
    }

    // ===== If / While =====

    @Test
    void testIfTrueExecutes() {
        EvalContext ctx = run("if (1) then done else move up");
        assertTrue(ctx.isDone());
    }

    @Test
    void testIfFalseExecutesElse() {
        EvalContext ctx = run("if (0) then move up else done");
        assertTrue(ctx.isDone());
    }

    @Test
    void testIfBranchEffect() {
        RecordGameAPI api = new RecordGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "if (1) then move up else shoot down 5");

        assertEquals(1, api.moveCount);
        assertEquals(0, api.shootCount);
    }

    @Test
    void testWhileFalseDoesNothing() {
        EvalContext ctx = run("while (0) move up");
        assertFalse(ctx.isDone());
    }

    @Test
    void testWhileLimitTerminates() {
        RecordGameAPI api = new RecordGameAPI();
        api.setBudget(20000);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "while (1) move up");

        assertTrue(api.moveCount > 0);
        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.LOOP_LIMIT,
                ctx.getTerminationReason());
    }

    // ===== Division =====

    @Test
    void testDivisionByZeroTerminates() {
        EvalContext ctx = run("x = 10/0");
        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.DIVISION_BY_ZERO,
                ctx.getTerminationReason());
    }

    @Test
    void testModuloByZeroTerminates() {
        EvalContext ctx = run("x = 5%0");
        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.DIVISION_BY_ZERO,
                ctx.getTerminationReason());
    }

    @Test
    void testDivisionStopsFurtherExecution() {
        EvalContext ctx = run("x = 10/0 y = 5");

        assertTrue(ctx.isDone());
        assertEquals(0, ctx.getVariable("y"));
    }

    // ===== Special variables =====

    @Test
    void testAssignSpecialNoEffect() {
        EvalContext ctx = run("Budget = 5");
        assertNotEquals(5, ctx.resolveIdentifier("Budget"));
    }

    @Test
    void testReadBudget() {
        RecordGameAPI api = new RecordGameAPI();
        api.setBudget(123);

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, "x = Budget");

        assertEquals(123, ctx.getVariable("x"));
    }

    // ===== Local / Global =====

    @Test
    void testLocalVariable() {
        EvalContext ctx = run("x = 5");
        assertEquals(5, ctx.getVariable("x"));
    }

    @Test
    void testGlobalVariable() {
        Map<String, Long> globals = new HashMap<>();
        RecordGameAPI api = new RecordGameAPI();
        EvalContext ctx = new EvalContext(api, globals);

        runWith(ctx, "X = 10");

        assertEquals(10, globals.get("X"));
    }

    @Test
    void testSharingGlobalVariableBetweenContexts() {
        Map<String, Long> globals = new HashMap<>();

        RecordGameAPI api1 = new RecordGameAPI();
        RecordGameAPI api2 = new RecordGameAPI();

        EvalContext ctx1 = new EvalContext(api1, globals);
        EvalContext ctx2 = new EvalContext(api2, globals);

        runWith(ctx1, "X = 7");

        assertEquals(7, ctx2.getVariable("X"));
    }

    @Test
    void testNearbyTrueBranch() {
        RecordGameAPI api = new RecordGameAPI();
        api.setNearby(strategy.ast.Direction.UPRIGHT, 1);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx,
                "if (nearby upright) then {move upleft done } else {shoot upright 2 done}");

        assertEquals(1, api.moveCount);
        assertEquals(0, api.shootCount);
    }

    @Test
    void testNearbyFalseBranch() {
        RecordGameAPI api = new RecordGameAPI();
        api.setNearby(strategy.ast.Direction.UPRIGHT, 0);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx,
                "if (nearby upright) then {move upleft done} else {shoot upright 2 done}");

        assertEquals(0, api.moveCount);
        assertEquals(1, api.shootCount);
    }

    @Test
    void testShootAllDirections() {
        RecordGameAPI api = new RecordGameAPI();
        Map<String, Long> global = new HashMap<>();

        api.setOpponent(11);

        String src = """
        op = opponent

        if (op % 10 - 1) then {} else {shoot up 5 done}
        if (op % 10 - 2) then {} else {shoot upright 5 done}
        if (op % 10 - 3) then {} else {shoot downright 5 done}
        if (op % 10 - 4) then {} else {shoot down 5 done}
        if (op % 10 - 5) then {} else {shoot downleft 5 done}
        shoot upleft 5 done
    """;

        Direction[] expected = {
                Direction.UP,
                Direction.UPRIGHT,
                Direction.DOWNRIGHT,
                Direction.DOWN,
                Direction.DOWNLEFT,
                Direction.UPLEFT
        };

        for (int i = 1; i <= 6; i++) {
            api.clearLogs();
            api.shootCount = 0;
            api.moveCount = 0;

            api.setBudget(100);
            api.setOpponent(10 + i);

            EvalContext ctx = new EvalContext(api, new HashMap<>()); // ✅ NEW ctx

            runWith(ctx, src);

            assertTrue(api.shootCount == 1);
            assertEquals(expected[i - 1], api.lastShootDir);
        }

    }

    @Test
    void testNearbyAllyVsEnemy() {
        RecordGameAPI api = new RecordGameAPI();
        Map<String, Long> global = new HashMap<>();

        String src = """
        if (nearby up) then {shoot up 5 done} else {move down done}
    """;

        // ===== enemy (positive) =====
        api.setBudget(100);
        api.setNearby(Direction.UP, 311); // enemy

        EvalContext ctx1 = new EvalContext(api, global);
        runWith(ctx1, src);

        assertEquals(1, api.shootCount);
        assertEquals(0, api.moveCount);

        // ===== reset =====
        api.clearLogs();
        api.shootCount = 0;
        api.moveCount = 0;

        // ===== ally (negative) =====
        api.setNearby(Direction.UP, -311); // ally

        EvalContext ctx2 = new EvalContext(api, global);
        runWith(ctx2, src);

        assertEquals(0, api.shootCount);
        assertEquals(1, api.moveCount);
    }

    @Test
    void testBudgetEdgeCases() {
        RecordGameAPI api = new RecordGameAPI();
        Map<String, Long> global = new HashMap<>();

        //ยิงไม่ได้ (budget ไม่พอ)
        api.setBudget(5); // ต้องใช้ 6
        EvalContext ctx1 = new EvalContext(api, global);

        runWith(ctx1, "shoot up 5");

        assertEquals(0, api.shootCount); // no-op
        assertFalse(ctx1.isDone()); // ไม่ terminate

        // ===== reset =====
        api.clearLogs();
        api.shootCount = 0;

        // ===== move ไม่ได้ (budget = 0) =====
        api.setBudget(0);
        EvalContext ctx2 = new EvalContext(api, global);

        runWith(ctx2, "move up");

        assertTrue(ctx2.isDone());
        assertEquals(EvalContext.TerminationReason.BUDGET_INSUFFICIENT,
                ctx2.getTerminationReason());
    }
}