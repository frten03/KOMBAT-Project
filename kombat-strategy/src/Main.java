import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import strategy.eval.GameAPI;
import strategy.eval.DummyGameAPI;

public class Main {

    public static void main(String[] args) {

        /*
        String src = """
        {
             t = 1;
             done;
             m = 0;
        }
        """;
        */

        String src = """
{
    t = t + 1
    m = 0

    while (3 - m) {

        if (Budget - 100)
        then {}
        else done

        opponentLoc = opponent

        if (opponentLoc / 10 - 1)
        then
            if (opponentLoc % 10 - 5)
            then move downleft
            else
                if (opponentLoc % 10 - 4)
                then move down
                else move up
        else
            if (opponentLoc)
            then {
                cost = 10 ^ (nearby up % 100 + 1)

                if (Budget - cost)
                then shoot up cost
                else {}
            }
            else {
                try = 0

                while (3 - try) {
                    success = 1
                    dir = random % 6

                    if ((dir - 4) * (nearby up % 10 + 1) ^ 2)
                    then move up
                    else success = 0

                    if (success)
                    then try = 3
                    else try = try + 1
                }
            }

        m = m + 1
    }
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
        System.out.println("Strategy finished. done = " + ctx.isDone());
    }
}
