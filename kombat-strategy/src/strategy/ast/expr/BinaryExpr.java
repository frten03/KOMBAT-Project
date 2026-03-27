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

        if (ctx.isDone()) return 0;

        long leftVal = left.eval(ctx);
        if (ctx.isDone()) return 0;

        long rightVal = right.eval(ctx);
        if (ctx.isDone()) return 0;

        return switch (op) {
            case PLUS -> leftVal + rightVal;

            case MINUS -> leftVal - rightVal;

            case MUL -> leftVal * rightVal;

            case DIV -> {
                if (rightVal == 0) {
                    ctx.terminate(EvalContext.TerminationReason.DIVISION_BY_ZERO);
                    yield 0;
                }
                yield leftVal / rightVal;
            }

            case MOD -> {
                if (rightVal == 0) {
                    ctx.terminate(EvalContext.TerminationReason.DIVISION_BY_ZERO);
                    yield 0;
                }
                yield leftVal % rightVal;
            }

            case POW -> (long) Math.pow(leftVal, rightVal);

            default -> throw new RuntimeException("Invalid operator");
        };
    }
}
