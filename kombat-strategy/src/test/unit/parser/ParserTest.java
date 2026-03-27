package test.unit.parser;

import org.junit.jupiter.api.Test;
import strategy.ast.core.Statement;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    private Statement parse(String src) {
        Tokenizer tz = new Tokenizer(src);
        Parser p = new Parser(tz);
        return p.parseStatement();
    }

    @Test
    void testParseMove() {
        assertNotNull(parse("move up"));
        assertNotNull(parse("move downright"));
    }

    @Test
    void testParseShoot() {
        assertNotNull(parse("shoot up 10"));
        assertNotNull(parse("shoot down (3+4)*2"));
    }

    @Test
    void testParseDone() {
        assertNotNull(parse("done"));
    }

    @Test
    void testParseIf() {
        assertNotNull(parse("if (1) then move up else done"));
        assertNotNull(parse("if (x - 1) then move up else move down"));
    }

    @Test
    void testParseWhile() {
        assertNotNull(parse("while (opponent) {shoot up 10 move up}"));
    }

    @Test
    void testParseBlock() {
        assertNotNull(parse("{ move up move down }"));
    }

    @Test
    void testInvalidSyntax() {
        assertThrows(RuntimeException.class,
                () -> parse("move")); // missing direction
    }

    @Test
    void testInvalidToken() {
        assertThrows(RuntimeException.class,
                () -> parse("fly up"));
    }
    // ขาด then
    @Test
    void testMissingThen() {
        String src = """
                if 1 {
                    move up
                } else {
                    done
                }
                """;

        assertThrows(RuntimeException.class, () -> parse(src));
    }

    // nearby ไม่มี direction
    @Test
    void testNearbyMissingDirection() {
        String src = """
                if nearby then move up else done
                """;

        assertThrows(RuntimeException.class, () -> parse(src));
    }
}
