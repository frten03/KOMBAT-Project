package test.unit.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Expression;
import strategy.eval.EvalContext;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import test.unit.testutil.DummyGameAPI;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

    private long eval(String expr) {
        Parser p = new Parser(new Tokenizer(expr));
        Expression e = p.parseExpression();
        EvalContext ctx = new EvalContext(new DummyGameAPI(), new HashMap<>());
        return e.eval(ctx);
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

    @Test
    void testDivisionByZeroTerminates() {
        Parser p = new Parser(new Tokenizer("10/0"));
        Expression e = p.parseExpression();

        EvalContext ctx = new EvalContext(new DummyGameAPI(), new HashMap<>());

        e.eval(ctx);

        assertTrue(ctx.isDone());
        assertEquals(EvalContext.TerminationReason.DIVISION_BY_ZERO,
                ctx.getTerminationReason());
    }

    @Test
    void testSpecialVariableBudget() {
        DummyGameAPI api = new DummyGameAPI();
        api.setBudget(1000);   // กำหนดค่า test เอง

        EvalContext ctx = new EvalContext(api, new HashMap<>());
        Parser p = new Parser(new Tokenizer("Budget"));
        Expression e = p.parseExpression();

        assertEquals(1000, e.eval(ctx));
    }
}