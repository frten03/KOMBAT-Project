package strategy.parser;

import strategy.ast.*;
import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.ast.expr.BinaryExpr;
import strategy.ast.expr.NumberExpr;
import strategy.ast.stmt.*;
import strategy.tokenizer.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Tokenizer tz;

    public Parser(Tokenizer tz) {
        this.tz = tz;
    }

    private Token eat(TokenType type) {
        Token t = tz.nextToken();
        if (t.type != type)
            throw new RuntimeException("Expected " + type);
        return t;
    }

    // expression = term ((+|-) term)*
    public Expression parseExpression() {
        Expression e = parseTerm();
        while (true) {
            TokenType t = tz.peekToken().type;
            if (t == TokenType.PLUS || t == TokenType.MINUS) {
                eat(t);
                e = new BinaryExpr(e, t, parseTerm());
            } else break;
        }
        return e;
    }

    // term = factor ((*|/) factor)*
    private Expression parseTerm() {
        Expression e = parseFactor();
        while (true) {
            TokenType t = tz.peekToken().type;
            if (t == TokenType.MUL || t == TokenType.DIV) {
                eat(t);
                e = new BinaryExpr(e, t, parseFactor());
            } else break;
        }
        return e;
    }

    private Expression parseFactor() {
        Token t = tz.nextToken();
        if (t.type == TokenType.NUMBER)
            return new NumberExpr(Long.parseLong(t.value));
        if (t.type == TokenType.LPAREN) {
            Expression e = parseExpression();
            eat(TokenType.RPAREN);
            return e;
        }
        throw new RuntimeException("Invalid factor");
    }

    public Statement parseStatement() {
        Token t = tz.peekToken();
        return switch (t.type) {
            case IF -> parseIf();
            case WHILE -> parseWhile();
            case LBRACE -> parseBlock();
            case DONE -> {
                eat(TokenType.DONE);
                eat(TokenType.SEMI);
                yield new DoneStmt();
            }
            case MOVE -> parseMove();
            case SHOOT -> parseShoot();
            default -> throw new RuntimeException("Unknown statement");
        };
    }

    private Statement parseIf() {
        eat(TokenType.IF);
        Expression cond = parseExpression();
        Statement body = parseStatement();
        return new IfStmt(cond, body);
    }

    private Statement parseWhile() {
        eat(TokenType.WHILE);
        Expression cond = parseExpression();
        Statement body = parseStatement();
        return new WhileStmt(cond, body);
    }

    private Statement parseBlock() {
        eat(TokenType.LBRACE);
        List<Statement> list = new ArrayList<>();
        while (tz.peekToken().type != TokenType.RBRACE)
            list.add(parseStatement());
        eat(TokenType.RBRACE);
        return new BlockStmt(list);
    }

    private Statement parseMove() {
        eat(TokenType.MOVE);
        Direction dir = parseDirection();
        eat(TokenType.SEMI);
        return new MoveStmt(dir);
    }

    private Statement parseShoot() {
        eat(TokenType.SHOOT);
        Direction dir = parseDirection();
        Expression cost = parseExpression();
        eat(TokenType.SEMI);
        return new ShootStmt(dir, cost);
    }

    private Direction parseDirection() {
        Token t = tz.nextToken();
        return switch (t.type) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case UPLEFT -> Direction.UPLEFT;
            case UPRIGHT -> Direction.UPRIGHT;
            case DOWNLEFT -> Direction.DOWNLEFT;
            case DOWNRIGHT -> Direction.DOWNRIGHT;
            default -> throw new RuntimeException("Expected direction");
        };
    }

}
