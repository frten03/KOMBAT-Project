package strategy.ast.expr;

import strategy.ast.Direction;
import strategy.ast.core.Expression;
import strategy.eval.EvalContext;

public class InfoExpr implements Expression {
    public InfoExpr(Kind kind, Direction dir) {
        this.kind = kind;
        this.dir = dir; // ใช้เฉพาะ NEARBY
    }

    public enum Kind { OPPONENT, NEARBY, ALLY }
    private final Kind kind;
    private final Direction dir;

    /*@Override
    public long eval(EvalContext ctx) {
        if (kind == Kind.OPPONENT)
            return ctx.api.opponent();   // ใช้ของจริงจากเกม //คืนค่าจำนวน opponent
        return ctx.api.nearby(dir); //ถาม API ว่าทิศนั้นมีอะไรไหม
    }
     */

    @Override
    public long eval(EvalContext ctx) {
        switch (kind) {
            case OPPONENT:
                return ctx.api.opponent(); // ใช้ของจริงจากเกม //คืนค่าจำนวน opponent
            case ALLY:
                return ctx.api.ally(); //คืนค่าจำนวน ally
            case NEARBY:
                return ctx.api.nearby(dir); //ถาม API ว่าทิศนั้นมีอะไรไหม
            default:
                throw new RuntimeException("Unknown InfoExpr kind");
        }
    }

    /*Strategy Text
            ↓
    Parser สร้าง InfoExpr
            ↓
    Evaluator เรียก eval()
            ↓
    InfoExpr เรียก GameAPI
            ↓
    GameAPI คืนค่าจาก game state จริง
*/
}

