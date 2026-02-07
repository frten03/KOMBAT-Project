package strategy.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Expression;
import strategy.eval.DummyGameAPI;
import strategy.eval.EvalContext;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

    @Test
    void operatorPrecedence() {
        Parser p = new Parser(new Tokenizer("10 + 5 * 2"));
        Expression e = p.parseExpression();

        long result = e.eval(new EvalContext(new DummyGameAPI()));
        assertEquals(20, result);
    }
}
