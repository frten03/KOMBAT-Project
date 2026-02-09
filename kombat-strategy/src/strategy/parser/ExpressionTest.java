package strategy.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Expression;
import strategy.eval.DummyGameAPI;
import strategy.eval.EvalContext;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

    private long eval(String expr) {
        Parser p = new Parser(new Tokenizer(expr));
        Expression e = p.parseExpression();
        return e.eval(new EvalContext(new DummyGameAPI()));
    }

    @Test
    void testNumber() {
        assertEquals(5, eval("5"));
    }

    @Test
    void testAddSub() {
        assertEquals(7, eval("3+4"));
        assertEquals(1, eval("5-4"));
    }

    @Test
    void testMulDiv() {
        assertEquals(12, eval("3*4"));
        assertEquals(5, eval("10/2"));
    }

    @Test
    void testPrecedence() {
        assertEquals(11, eval("3+4*2"));
    }

    @Test
    void testParen() {
        assertEquals(14, eval("(3+4)*2"));
    }
}
