package strategy.ast.stmt;

import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import java.util.List;

public class BlockStmt implements Statement {
    private final List<Statement> statements;

    public BlockStmt(List<Statement> statements) {
        this.statements = statements;
    }

    public void eval(EvalContext ctx) {
        for (Statement s : statements) {
            if (ctx.isEvaluationStopped()) return;
            s.eval(ctx);
        }
    }
}
