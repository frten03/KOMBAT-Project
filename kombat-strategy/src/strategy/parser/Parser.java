package strategy.parser;

import strategy.ast.*;
import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.ast.expr.BinaryExpr;
import strategy.ast.expr.InfoExpr;
import strategy.ast.expr.NumberExpr;
import strategy.ast.expr.VarExpr;
import strategy.ast.stmt.*;
import strategy.tokenizer.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Tokenizer tz;

    public Parser(Tokenizer tz) {
        this.tz = tz;
    }

    private Token consume(TokenType type) {
        Token t = tz.nextToken();
        if (t.type != type)
            throw new RuntimeException("Expected " + type);
        return t;
    }

    public Expression parseExpression() {
        return parseAddSub();
    }

    // AddSub = term ((+|-) term)
    private Expression parseAddSub() {
        Expression e = parseTerm();

        while (true) {
            TokenType t = tz.peekToken().type;
            if (t == TokenType.PLUS || t == TokenType.MINUS) {
                consume(t);
                e = new BinaryExpr(e, t, parseTerm());
            } else break;
        }

        return e;
    }

    // term = factor ((*|/) factor)
    private Expression parseTerm() {
        Expression e = parseFactor();
        while (true) {
            TokenType t = tz.peekToken().type;
            if (t == TokenType.MUL || t == TokenType.DIV || t == TokenType.MOD) {
                consume(t);
                e = new BinaryExpr(e, t, parseFactor());
            } else break;
        }
        return e;
    }

    private Expression parseFactor() {
        Expression left = parsePower();

        if (tz.peekToken().type == TokenType.POW) {   // ^
            consume(TokenType.POW);
            Expression right = parseFactor(); // ⚠ recursive = right associative
            return new BinaryExpr(left, TokenType.POW, right);
        }

        return left;
    }

    // parseFactor -> parsePower
    private Expression parsePower() {
        Token t = tz.peekToken();

        switch (t.type) {
            case NUMBER:
                consume(TokenType.NUMBER);
                return new NumberExpr(Long.parseLong(t.value));

            case LPAREN: {
                consume(TokenType.LPAREN);
                Expression e = parseExpression();
                consume(TokenType.RPAREN);
                return e;
            }

            case OPPONENT:
                // opponent → delegate to GameAPI later
                consume(TokenType.OPPONENT);
                return new InfoExpr(InfoExpr.Kind.OPPONENT, null);

            case NEARBY: {
                consume(TokenType.NEARBY);
                Direction dir = parseDirection();
                return new InfoExpr(InfoExpr.Kind.NEARBY, dir);

            }

            case ALLY:
                consume(TokenType.ALLY);
                return new InfoExpr(InfoExpr.Kind.ALLY, null);


            case IDENT:
                consume(TokenType.IDENT);
                return new VarExpr(t.value);

            default:
                throw new RuntimeException("Invalid factor: " + t.type);
        }
    }


    public Statement parseStrategy() {
        List<Statement> stmts = new ArrayList<>();

        while (tz.peekToken().type != TokenType.EOF) {
            stmts.add(parseStatement());
        }

        return new BlockStmt(stmts);
    }

    public Statement parseStatement() {
        Token t = tz.peekToken();
        return switch (t.type) {
            case IF -> parseIf();
            case WHILE -> parseWhile();
            case LBRACE -> parseBlock();
            case DONE -> {
                consume(TokenType.DONE);
                //consume(TokenType.SEMI);
                yield new DoneStmt();
            }
            case MOVE -> parseMove();
            case SHOOT -> parseShoot();
            case IDENT -> parseAssign();
            
            default -> throw new RuntimeException("Unknown statement");
        };
    }

    private Statement parseAssign() {
        Token name = consume(TokenType.IDENT);
        consume(TokenType.ASSIGN);
        Expression expr = parseExpression();
        //consume(TokenType.SEMI);

        return new AssignmentStmt(name.value, expr);
    }

    private Statement parseIf() {
        consume(TokenType.IF);

        consume(TokenType.LPAREN);
        Expression cond = parseExpression();
        consume(TokenType.RPAREN);

        consume(TokenType.THEN);
        Statement thenStmt = parseStatement();

        consume(TokenType.ELSE);
        Statement elseStmt = parseStatement();

        return new IfStmt(cond, thenStmt, elseStmt);
    }


    private Statement parseWhile() {
        consume(TokenType.WHILE);

        consume(TokenType.LPAREN);
        Expression cond = parseExpression();
        consume(TokenType.RPAREN);

        Statement body = parseStatement();

        return new WhileStmt(cond, body);
    }

    private Statement parseBlock() {
        consume(TokenType.LBRACE);
        List<Statement> list = new ArrayList<>();
        while (tz.peekToken().type != TokenType.RBRACE)
            list.add(parseStatement());
        consume(TokenType.RBRACE);
        return new BlockStmt(list);
    }

    private Statement parseMove() {
        consume(TokenType.MOVE);
        Direction dir = parseDirection();
        //consume(TokenType.SEMI);
        return new MoveStmt(dir);
    }

    private Statement parseShoot() {
        consume(TokenType.SHOOT);
        Direction dir = parseDirection();
        Expression cost = parseExpression();
        //consume(TokenType.SEMI);
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
