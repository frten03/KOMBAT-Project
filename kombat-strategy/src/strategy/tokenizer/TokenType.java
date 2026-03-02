package strategy.tokenizer;

public enum TokenType {
    IF,THEN, ELSE, WHILE, DONE,
    IDENT, NUMBER,
    PLUS, MINUS, MUL, DIV, MOD, POW,
    EQ, // ==
    ASSIGN,
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    SEMI,
    EOF,
    MOVE, SHOOT,
    UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT,
    OPPONENT, NEARBY, ALLY,
    }
