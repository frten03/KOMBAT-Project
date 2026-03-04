package game;// Demo.java
import game.*;
import strategy.eval.*;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;
import strategy.ast.core.Statement;

import java.util.HashMap;

public class Demo {

    public static void main(String[] args) {

        // Parser error test
        String badStrategy = """
            if (opponent) then { move up; }
        """;

        try {
            Parser parser = new Parser(new Tokenizer(badStrategy));
            parser.parseStatement();
            System.out.println("SHOULD NOT REACH HERE");
        } catch (RuntimeException e) {
            System.out.println("Parse failed as expected:");
            System.out.println(e.getMessage());
        }

        System.out.println("DEMO START");

        // config setup
        GameConfig config = new GameConfig(
                100,
                1000,
                100,
                50,
                10,
                1000,
                5,
                10,
                5
        );

        GameState gameState = new GameState(config, GameMode.DUEL);

        Player p1 = gameState.getPlayer1();
        Player p2 = gameState.getPlayer2();


        //----- Strategy source -----
        // check block stmt
        String src = """
            {
                move up;
                shoot up 2;
            }
        """;

        // check if stmt
//        String src = """
//                if (opponent) then {
//                    shoot up 2;
//                    move down;
//                } else {
//                    move up;
//                }
//          """;

        // check while stmt (shoot until opponent die)
//        String src = """
//            while (opponent) {
//                shoot up 1;
//            }
//            move down;
//        """;

        // check while stmt (move until hit the edge)
        // ชนขอบด้านล่าง
//        String src = """
//                while (opponent) move down;
//        """;
        // ชนขอบด้านข้าง
//        String src = """
//                while (opponent) move upleft;
//        """;
        // ชน minion ด้านบนไม่ขยับ
//        String src = """
//                while (opponent) move up;
//        """;

        // check ally
//        String src = """
//                if (nearby upright == 1) then {
//                    move upleft;
//                } else {
//                    shoot upright 2;
//                }
//        """;

        // check opponent
//        String src = """
//                if(nearby up == 2) then {
//                     shoot up 2;
//                } else {
//                }
//        """;

        // Game setup
        MinionType warrior = new MinionType(
                "Warrior",
                1,
                src // ใช้ strategy เดียวกับที่กำลัง test
        );

        p1.addMinionType(warrior);
        p2.addMinionType(warrior);

        // lock setup
        gameState.lockSetupPhase();
        System.out.println("Setup locked.");

        gameState.startTurn();

        boolean spawn1 = gameState.spawnMinion("Warrior", 0, 0);
        System.out.println("P1 spawn success = " + spawn1);

        gameState.endTurn();

        gameState.startTurn();

        boolean spawn2 = gameState.spawnMinion("Warrior", 7, 7);
        System.out.println("P2 spawn success = " + spawn2);

        gameState.endTurn();

        if (!spawn1 || !spawn2) {
            System.out.println("Spawn failed. Stop demo.");
            return;
        }

        Minion m1 = p1.getMinions().get(0);
        Minion m2 = p2.getMinions().get(0);

        System.out.println("After spawn:");
        System.out.println("P1 budget = " + p1.getBudget());
        System.out.println("P1 minions = " + p1.getMinions().size());

        Grid grid = gameState.getBoard();

        GameLogic logic = new GameLogic(grid, p1, p2, config);

        Parser parser = new Parser(new Tokenizer(src));
        Statement strategy = parser.parseStatement();

        // Bridge
        GameAPI api = new RealGameAPI(logic, m1);
        EvalContext ctx = new EvalContext(api, new HashMap<>());

        // Execute strategy
        strategy.eval(ctx);

        // Result
        System.out.println("After strategy:");
        System.out.println(m1);
        System.out.println(m2);
//        System.out.println(m3);
        System.out.println("P1 budget = " + p1.getBudget());

        System.out.println("=== DEMO END ===");
    }
}
