package strategy.ast.expr;

import strategy.ast.core.Expression;
import strategy.eval.EvalContext;
import strategy.tokenizer.TokenType;

public class BinaryExpr implements Expression {
    private final Expression left, right;
    private final TokenType op;

    public BinaryExpr(Expression left, TokenType op, Expression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public long eval(EvalContext ctx) {
        return switch (op) {
            case PLUS -> left.eval(ctx) + right.eval(ctx);
            case MINUS -> left.eval(ctx) - right.eval(ctx);
            case MUL -> left.eval(ctx) * right.eval(ctx);
            case DIV -> left.eval(ctx) / right.eval(ctx);
            default -> throw new RuntimeException("Invalid operator");
        };
    }
}
