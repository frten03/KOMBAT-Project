package strategy.ast.core;

import strategy.eval.EvalContext;

public interface Expression extends ASTNode {
    long eval(EvalContext ctx);
}
