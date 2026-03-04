package strategy.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.Direction;
import strategy.ast.expr.InfoExpr;
import strategy.eval.DummyGameAPI;
import strategy.eval.EvalContext;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class InfoExprTest {

    @Test
    void testOpponent() {
        EvalContext ctx = new EvalContext(new DummyGameAPI(), new HashMap<>());
        InfoExpr e = new InfoExpr(InfoExpr.Kind.OPPONENT, null);
        assertEquals(0, e.eval(ctx));
    }

    @Test
    void testNearby() {
        EvalContext ctx = new EvalContext(new DummyGameAPI(), new HashMap<>());
        InfoExpr e = new InfoExpr(InfoExpr.Kind.NEARBY, Direction.UP);
        assertEquals(0, e.eval(ctx));
    }

    @Test
    void testAlly(){
        EvalContext ctx = new EvalContext(new DummyGameAPI(), new HashMap<>());
        InfoExpr e = new InfoExpr(InfoExpr.Kind.ALLY, null);
        assertEquals(0, e.eval(ctx));
    }
}