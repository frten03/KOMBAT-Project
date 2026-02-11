package strategy.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.Direction;
import strategy.ast.expr.InfoExpr;
import strategy.eval.DummyGameAPI;
import strategy.eval.EvalContext;

import static org.junit.jupiter.api.Assertions.*;

public class InfoExprTest {

    @Test
    void testOpponent() {
        InfoExpr e = new InfoExpr(InfoExpr.Kind.OPPONENT, null);
        assertEquals(0, e.eval(new EvalContext(new DummyGameAPI())));
    }

    @Test
    void testNearby() {
        InfoExpr e = new InfoExpr(InfoExpr.Kind.NEARBY, Direction.UP);
        assertEquals(1, e.eval(new EvalContext(new DummyGameAPI())));
    }

    @Test
    void testAlly(){
        InfoExpr e = new InfoExpr(InfoExpr.Kind.ALLY, null);
        assertEquals(0, e.eval(new EvalContext(new DummyGameAPI())));
    }
}
