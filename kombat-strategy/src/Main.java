import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import strategy.eval.GameAPI;
import strategy.eval.DummyGameAPI;

public class Main {

    public static void main(String[] args) {

        // ===== ตัวอย่าง strategy language =====
        String src = """
        {
            move up;
            move upright;
            shoot down 10 + 5;
            done;
            move down;
        }
        """;

        // ===== สร้าง GameAPI แบบ dummy =====
        GameAPI api = new DummyGameAPI();

        // ===== EvalContext (หนึ่ง minion, หนึ่ง turn) =====
        EvalContext ctx = new EvalContext(api);

        // ===== Parse =====
        Parser parser = new Parser(new Tokenizer(src));
        Statement strategy = parser.parseStatement();

        // ===== Evaluate =====
        strategy.eval(ctx);

        // ===== สรุปผล =====
        System.out.println("Strategy finished. done = " + ctx.isEvaluationStopped());
    }
}
