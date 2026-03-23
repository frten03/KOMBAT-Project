package test.unit.tokenizer;

import org.junit.jupiter.api.Test;
import strategy.tokenizer.Token;
import strategy.tokenizer.TokenType;
import strategy.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void testSimpleAssignment() {
        Tokenizer tz = new Tokenizer("x = 5 + 3");

        assertEquals(TokenType.IDENT, tz.nextToken().type);
        assertEquals(TokenType.ASSIGN, tz.nextToken().type);
        assertEquals(TokenType.NUMBER, tz.nextToken().type);
        assertEquals(TokenType.PLUS, tz.nextToken().type);
        assertEquals(TokenType.NUMBER, tz.nextToken().type);
    }

    @Test
    void testKeywords() {
        Tokenizer tz = new Tokenizer("if then else while done move shoot");

        assertEquals(TokenType.IF, tz.nextToken().type);
        assertEquals(TokenType.THEN, tz.nextToken().type);
        assertEquals(TokenType.ELSE, tz.nextToken().type);
        assertEquals(TokenType.WHILE, tz.nextToken().type);
        assertEquals(TokenType.DONE, tz.nextToken().type);
        assertEquals(TokenType.MOVE, tz.nextToken().type);
        assertEquals(TokenType.SHOOT, tz.nextToken().type);
    }

    @Test
    void testDirections() {
        Tokenizer tz = new Tokenizer("up down upleft upright downleft downright");

        assertEquals(TokenType.UP, tz.nextToken().type);
        assertEquals(TokenType.DOWN, tz.nextToken().type);
        assertEquals(TokenType.UPLEFT, tz.nextToken().type);
        assertEquals(TokenType.UPRIGHT, tz.nextToken().type);
        assertEquals(TokenType.DOWNLEFT, tz.nextToken().type);
        assertEquals(TokenType.DOWNRIGHT, tz.nextToken().type);
    }

    @Test
    void testInfoExpressions() {
        Tokenizer tz = new Tokenizer("opponent nearby ally");

        assertEquals(TokenType.OPPONENT, tz.nextToken().type);
        assertEquals(TokenType.NEARBY, tz.nextToken().type);
        assertEquals(TokenType.ALLY, tz.nextToken().type);
    }

    @Test
    void testOperators() {
        Tokenizer tz = new Tokenizer("+ - * / % ^ == =");

        assertEquals(TokenType.PLUS, tz.nextToken().type);
        assertEquals(TokenType.MINUS, tz.nextToken().type);
        assertEquals(TokenType.MUL, tz.nextToken().type);
        assertEquals(TokenType.DIV, tz.nextToken().type);
        assertEquals(TokenType.MOD, tz.nextToken().type);
        assertEquals(TokenType.POW, tz.nextToken().type);
        assertEquals(TokenType.EQ, tz.nextToken().type);
        assertEquals(TokenType.ASSIGN, tz.nextToken().type);
    }



    @Test
    void testParenthesesAndBraces() {
        Tokenizer tz = new Tokenizer("( ) { }");

        assertEquals(TokenType.LPAREN, tz.nextToken().type);
        assertEquals(TokenType.RPAREN, tz.nextToken().type);
        assertEquals(TokenType.LBRACE, tz.nextToken().type);
        assertEquals(TokenType.RBRACE, tz.nextToken().type);
    }

    @Test
    void testNumberParsing() {
        Tokenizer tz = new Tokenizer("12345");

        Token token = tz.nextToken();
        assertEquals(TokenType.NUMBER, token.type);
        assertEquals("12345", token.value);
    }

    @Test
    void testIdentifierParsing() {
        Tokenizer tz = new Tokenizer("abc123");

        Token token = tz.nextToken();
        assertEquals(TokenType.IDENT, token.type);
        assertEquals("abc123", token.value);
    }

    @Test
    void testPeekTokenDoesNotAdvance() {
        Tokenizer tz = new Tokenizer("x");

        Token t1 = tz.peekToken();
        Token t2 = tz.peekToken();

        assertEquals(t1.type, t2.type);
        assertEquals(TokenType.IDENT, tz.nextToken().type); // ยังต้องเป็นตัวเดิม
    }

    @Test
    void testEOF() {
        Tokenizer tz = new Tokenizer("");

        assertEquals(TokenType.EOF, tz.nextToken().type);
    }

    @Test
    void testWhitespaceIgnored() {
        Tokenizer tz = new Tokenizer("   x   ");

        assertEquals(TokenType.IDENT, tz.nextToken().type);
    }

    @Test
    void testInvalidCharacterThrows() {
        Tokenizer tz = new Tokenizer("@");

        assertThrows(RuntimeException.class, tz::nextToken);
    }

    @Test
    void debugTokenization() {
        Tokenizer tz = new Tokenizer("nearby upright == 1");

        while (true) {
            Token t = tz.nextToken();
            System.out.println(t.type + " : " + t.value);
            if (t.type == TokenType.EOF) break;
        }
    }
}