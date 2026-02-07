package strategy.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Statement;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parseMoveStatement() {
        String src = "{ move up; }";
        Parser p = new Parser(new Tokenizer(src));

        Statement s = p.parseStatement();
        assertNotNull(s);
    }

    @Test
    void parseMoveAndShoot() {
        String src = """
        {
            move up;
            shoot down 10+5;
        }
        """;

        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();
        assertNotNull(s);
    }

    @Test
    void parseIfWhileBlock() {
        String src = """
        {
            while 1 {
                if 1 done;
            }
        }
        """;

        Parser p = new Parser(new Tokenizer(src));
        Statement s = p.parseStatement();
        assertNotNull(s);
    }

    @Test
    void syntaxErrorMissingSemicolon() {
        String src = "{ move up }"; // ไม่มี ;

        Parser p = new Parser(new Tokenizer(src));
        assertThrows(RuntimeException.class, p::parseStatement);
    }

    @Test
    void syntaxErrorUnknownKeyword() {
        String src = "{ move banana; }";

        Parser p = new Parser(new Tokenizer(src));
        assertThrows(RuntimeException.class, p::parseStatement);
    }
}
