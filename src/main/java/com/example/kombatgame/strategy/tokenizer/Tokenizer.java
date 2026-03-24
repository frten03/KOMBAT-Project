package com.example.kombatgame.strategy.tokenizer;

public class Tokenizer {
    private final String src;
    private int pos = 0;

    public Tokenizer(String src) {
        this.src = src;
    }

    public Token peekToken() {
        int save = pos;
        Token t = nextToken();
        pos = save;
        return t;
    }

    public Token nextToken() {
        skipSpace();
        if (pos >= src.length())
            return new Token(TokenType.EOF, "");

        char c = src.charAt(pos);

        if (Character.isDigit(c)) return number();
        if (Character.isLetter(c)) return ident();

        pos++;

        switch (c) {

            case '+':
                return new Token(TokenType.PLUS, "+");

            case '-':
                return new Token(TokenType.MINUS, "-");

            case '*':
                return new Token(TokenType.MUL, "*");

            case '/':
                return new Token(TokenType.DIV, "/");

            case '%':
                return new Token(TokenType.MOD, "%");

            case '^':
                return new Token(TokenType.POW, "^");

            case '=':
                if (pos < src.length() && src.charAt(pos) == '=') {
                    pos++;
                    return new Token(TokenType.EQ, "==");
                }
                return new Token(TokenType.ASSIGN, "=");
                //throw new RuntimeException("Single '=' not allowed");

            case '(':
                return new Token(TokenType.LPAREN, "(");

            case ')':
                return new Token(TokenType.RPAREN, ")");

            case '{':
                return new Token(TokenType.LBRACE, "{");

            case '}':
                return new Token(TokenType.RBRACE, "}");

            /**case ';':
                return new Token(TokenType.SEMI, ";");
            **/
            default:
                throw new RuntimeException("Unexpected char: " + c);
        }

    }

    private void skipSpace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos)))
            pos++;
    }

    private Token number() {
        int start = pos;
        while (pos < src.length() && Character.isDigit(src.charAt(pos)))
            pos++;
        return new Token(TokenType.NUMBER, src.substring(start, pos));
    }

    private Token ident() {
        int start = pos;

        pos++;

        while (pos < src.length() &&
                Character.isLetterOrDigit(src.charAt(pos)))
            pos++;
        String s = src.substring(start, pos);

        return switch (s) {
            case "if" -> new Token(TokenType.IF, s);
            case "then" -> new Token(TokenType.THEN, s);
            case "else" -> new Token(TokenType.ELSE, s);

            case "while" -> new Token(TokenType.WHILE, s);
            case "done" -> new Token(TokenType.DONE, s);
            case "move" -> new Token(TokenType.MOVE, s);
            case "shoot" -> new Token(TokenType.SHOOT, s);

            // ===== INFO EXPRESSIONS =====
            case "opponent" -> new Token(TokenType.OPPONENT, s);
            case "nearby" -> new Token(TokenType.NEARBY, s);
            case "ally" -> new Token(TokenType.ALLY, s);

            // ===== DIRECTION =====
            case "up" -> new Token(TokenType.UP, s);
            case "down" -> new Token(TokenType.DOWN, s);
            case "upleft" -> new Token(TokenType.UPLEFT, s);
            case "upright" -> new Token(TokenType.UPRIGHT, s);
            case "downleft" -> new Token(TokenType.DOWNLEFT, s);
            case "downright" -> new Token(TokenType.DOWNRIGHT, s);

            default -> new Token(TokenType.IDENT, s);
        };
    }

}
