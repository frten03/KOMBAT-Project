package strategy.ast.stmt;

import strategy.ast.core.Statement;
import strategy.eval.EvalContext;

public class DoneStmt implements Statement {
    public void eval(EvalContext ctx) {
        ctx.stopEvaluation();
    }
}
