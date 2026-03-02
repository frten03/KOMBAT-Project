package strategy.eval;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Statement;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluatorTest {

    private EvalContext run(String src) {
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();
        EvalContext ctx = new EvalContext(new DummyGameAPI());
        s.eval(ctx);
        return ctx;
    }

    @Test
    void testMoveExecutes() {
        EvalContext ctx = run("move up;");
        assertFalse(ctx.isDone());
    }

    @Test
    void testShootExecutes() {
        EvalContext ctx = run("shoot down 10;");
        assertFalse(ctx.isDone());
    }

    @Test
    void testDoneStops() {
        EvalContext ctx = run("done;");
        assertTrue(ctx.isDone());
    }

    @Test
    void testIfTrueExecutes() {
        EvalContext ctx = run("if 1 then done; else move up;");
        assertTrue(ctx.isDone());
    }

    @Test
    void testIfFalseSkips() {
        EvalContext ctx = run("if 0 then move up; else done;");
        assertTrue(ctx.isDone());
    }

    @Test
    void testWhileTerminates() {
        EvalContext ctx = run("while 0 move up;");
        assertFalse(ctx.isDone());
    }

    @Test
    void testWhileLimit() {
        Parser p = new Parser(new Tokenizer("while 1 move up;"));
        Statement s = p.parseStatement();
        EvalContext ctx = new EvalContext(new DummyGameAPI());

        assertDoesNotThrow(() -> s.eval(ctx));
    }

    @Test
    void testNearbyCompare() {
        String src = "if nearby upright == 1 then {" +
                "    move upleft;" +
                "} else {" +
                "    shoot upright 2;" +
                "}";
        EvalContext ctx = run(src);
        // assert ตาม DummyGameAPI
    }
}
