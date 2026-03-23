package test.unit.eval;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import test.unit.testutil.DummyGameAPI;
import test.unit.testutil.RecordingDummyGameAPI;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    //helper
    private EvalContext run(String src) {
        DummyGameAPI api = new DummyGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, src);
        return ctx;
    }

    private void runWith(EvalContext ctx, String src) {
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStrategy();
        s.eval(ctx);
    }

    // Basic execution tests

    @Test
    void testMoveExecutes() {
        EvalContext ctx = run("move up");
        assertFalse(ctx.isDone());
    }

    @Test
    void testMoveBudgetInsufficientTerminates() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
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
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setBudget(5); // need 6 (5 + 1)

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, "shoot up 5");

        assertFalse(ctx.isDone());
        assertEquals(0, api.shootCount);
    }

    @Test
    void testDoneStops() {
        EvalContext ctx = run("done");
        assertTrue(ctx.isDone());
    }

    @Test
    void testDoneStopsFurtherExecution() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "done move up");

        assertEquals(0, ((RecordingDummyGameAPI) ctx.api).moveCount);
    }

    // If / While


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
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
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
    void testWhileLimitDoesNotCrash() {
        //EvalContext ctx = run("while 1 move up");
        assertDoesNotThrow(() -> run("while (1) move up"));
    }

    @Test
    void testWhileLoopLimitTerminates() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setBudget(20000); // สำคัญมาก

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "while (1) move up");

        assertTrue(api.moveCount > 0);
        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.LOOP_LIMIT,
                ctx.getTerminationReason());
    }


    // Division by zero


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
        assertEquals(0, ctx.getVariable("y")); // ต้องไม่ execute ต่อ
    }


    // Special variables


    @Test
    void testAssignSpecialNoEffect() {
        EvalContext ctx = run("Budget = 5");
        assertNotEquals(5, ctx.resolveIdentifier("Budget"));
    }

    @Test
    void testReadSpecialVariable() {
        DummyGameAPI api = new DummyGameAPI();
        api.setBudget(123);

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        runWith(ctx, "x = Budget");

        assertEquals(123, ctx.getVariable("x"));
    }


    // Local variables


    @Test
    void testLocalVariable() {
        EvalContext ctx = run("x = 5");
        assertEquals(5, ctx.getVariable("x"));
    }


    // Global variables


    @Test
    void testGlobalVariable() {
        Map<String, Long> globals = new HashMap<>();
        DummyGameAPI api = new DummyGameAPI();
        EvalContext ctx = new EvalContext(api, globals);

        runWith(ctx, "X = 10");

        assertEquals(10, globals.get("X"));
    }

    @Test
    void testSharingGlobalVariableBetweenContexts() {
        Map<String, Long> globals = new HashMap<>();

        DummyGameAPI api1 = new DummyGameAPI();
        DummyGameAPI api2 = new DummyGameAPI();

        EvalContext ctx1 = new EvalContext(api1, globals);
        EvalContext ctx2 = new EvalContext(api2, globals);

        runWith(ctx1, "X = 7");

        assertEquals(7, ctx2.getVariable("X"));
    }


    // RecordingDummyGameAPI tests


    @Test
    void testRecordingMove() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "move up");

        assertEquals(1, api.moveCount);
        assertEquals(strategy.ast.Direction.UP, api.lastMoveDir);
    }

    @Test
    void testRecordingShoot() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        runWith(ctx, "shoot down 5");

        assertEquals(1, api.shootCount);
        assertEquals(strategy.ast.Direction.DOWN, api.lastShootDir);
        assertEquals(5, api.lastShootCost);
    }
    // EQ(==) test
    @Test
    void testEquality() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        EvalContext ctx1 = new EvalContext(api, new HashMap<>());
        Parser p1 = new Parser(new Tokenizer("1 == 1"));
        Expression e1 = p1.parseExpression();
        EvalContext ctx2 = new EvalContext(api, new HashMap<>());
        Parser p2 = new Parser(new Tokenizer("1 == 2"));
        Expression e2 = p2.parseExpression();
        assertEquals(1, e1.eval(ctx1));
        assertEquals(0, e2.eval(ctx2));
    }

    // Nearby comparison


    @Test
    void debugNearbyValue() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setNearby(1);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        Parser p = new Parser(new Tokenizer("nearby upright"));
        Expression e = p.parseExpression();

        assertEquals(1, e.eval(ctx)); // ← ถ้า fail = parser / info expr พัง
    }

    @Test
    void debugNearbyEquality() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setNearby(1);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        Parser p = new Parser(new Tokenizer("nearby upright == 1"));
        Expression e = p.parseExpression();

        assertEquals(1, e.eval(ctx)); // ← ถ้า FAIL = parser precedence พัง
    }

    @Test
    void testNearbyCompareBranch() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setNearby(1);  // condition true

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        String src =
                "if (nearby upright == 1) then move upleft else shoot upright 2";

        runWith(ctx, src);

        assertEquals(1, api.moveCount);
        assertEquals(0, api.shootCount);
    }

    @Test
    void testNearbyFalseBranch() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        api.setNearby(0);

        EvalContext ctx = new EvalContext(api, new HashMap<>());

        String src =
                "if (nearby upright == 1) then move upleft else shoot upright 2";

        runWith(ctx, src);

        assertEquals(0, api.moveCount);
        assertEquals(1, api.shootCount);
    }

    /*@Test
    void testFullStrategy() {
        RecordingDummyGameAPI api = new RecordingDummyGameAPI();
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        String src = """
        x = 1
        while (x) {
            move up
            x = x - 1
        }
    """;

        runWith(ctx, src);

        assertEquals(1, api.moveCount);
    }

    @Test
    void debugAssignment() {
        EvalContext ctx = run("x = 1");

        assertEquals(1, ctx.getVariable("x")); // ← ถ้า FAIL = เจอ bug
    }
     */
}