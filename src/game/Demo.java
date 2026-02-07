package game;// Demo.java
import game.*;
import strategy.eval.*;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import strategy.ast.core.Statement;

public class Demo {

    public static void main(String[] args) {
        System.out.println("=== DEMO BEFORE START ===");

        String badStrategy = """
            if (opponent) then { move north; }
        """;

        try {
            Parser parser = new Parser(new Tokenizer(badStrategy));
            parser.parseStatement();   // ← ใช้อันนี้แทน parse()
            System.out.println("❌ SHOULD NOT REACH HERE");
        } catch (RuntimeException e) {
            System.out.println("Parse failed as expected:");
            System.out.println(e.getMessage());
        }

        System.out.println("=== DEMO BEFORE END ===");


        System.out.println("=== DEMO START ===");

        // ----- Game setup -----
        Grid grid = new Grid();
        Player p1 = new Player(20);
        Player p2 = new Player(20);

        Minion m1 = new Minion(3, 3, 10, 1, p1);
        Minion m2 = new Minion(2, 3, 6, 0, p2);

        grid.addMinion(m1);
        grid.addMinion(m2);

        GameLogic logic = new GameLogic(grid);

        // ----- Strategy source -----
        String src = """
            {
                move up;
                shoot up 2;
            }
        """;

        // ----- Parse strategy -----
        Parser parser = new Parser(new Tokenizer(src));
        Statement strategy = parser.parseStatement();

        // ----- Bridge -----
        GameAPI api = new RealGameAPI(logic, m1);
        EvalContext ctx = new EvalContext(api);

        // ----- Execute strategy -----
        strategy.eval(ctx);

        // ----- Result -----
        System.out.println("After strategy:");
        System.out.println(m1);
        System.out.println(m2);
        System.out.println("P1 budget = " + p1.getBudget());

        System.out.println("=== DEMO END ===");
    }
}
