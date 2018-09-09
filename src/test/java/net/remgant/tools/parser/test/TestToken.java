package net.remgant.tools.parser.test;

import net.remgant.tools.parser.Token;

import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public class TestToken extends Token {

    @CharToken
    final public static Predicate<Token> EQUALS = (t) -> t.getValue().equals("=");
    @CharToken
    final public static Predicate<Token> LEFT_PAREN = (t) -> t.getValue().equals("(");
    @CharToken
    final public static Predicate<Token> RIGHT_PAREN = (t) -> t.getValue().equals(")");
    @CharToken
    final public static Predicate<Token> SEMI_COLON = (t) -> t.getValue().equals(";");
    @CharToken
    final public static Predicate<Token> PLUS = (t) -> t.getValue().equals("+");
    @CharToken
    final public static Predicate<Token> MINUS = (t) -> t.getValue().equals("-");

    static {
        init(TestToken.class);
    }

}
