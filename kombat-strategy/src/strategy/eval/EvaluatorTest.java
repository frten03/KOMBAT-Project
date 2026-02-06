package strategy.eval;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Statement;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void executeMoveAndShoot() {
        String src = """
        {
            move up;
            shoot down 10;
        }
        """;

        EvalContext ctx = new EvalContext(new DummyGameAPI());
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();

        assertDoesNotThrow(() -> s.eval(ctx));
        assertFalse(ctx.isEvaluationStopped());
    }

    @Test
    void doneStopsExecution() {
        String src = """
        {
            move up;
            done;
            move down;
        }
        """;

        EvalContext ctx = new EvalContext(new DummyGameAPI());
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();

        s.eval(ctx);
        assertTrue(ctx.isEvaluationStopped());
    }

    @Test
    void whileLoopDoesNotInfinite() {
        String src = """
        {
            while 1 {
                move up;
            }
        }
        """;

        EvalContext ctx = new EvalContext(new DummyGameAPI());
        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();

        assertDoesNotThrow(() -> s.eval(ctx));
    }
}
