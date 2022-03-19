/*
   Copyright 2019 Jeffrey D. Remillard

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package net.remgant.tools.parser;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class for specifying and handling tokens.
 */
public abstract class Token {

    protected String value;
    protected Predicate<Token> predicate;

    public String getValue() {
        return value;
    }

    public Predicate<Token> getPredicate() {
        return predicate;
    }

    protected Token(String v) {
        this.value = v;
        this.predicate = t -> t.getValue().equalsIgnoreCase(v);
    }

    protected Token() {

    }

    private Token(String value, Predicate<Token> predicate) {
        this.value = value;
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equal(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    // Some nested classes for some basic token types

    /**
     * A Token that represents a Keyword.
     */
    public static class Keyword extends Token {
        public Keyword(String value) {
            super(value);
        }
    }

    /**
     * A Token that is a single character.
     */
    public static class Char extends Token {
        public Char(String value) {
            super(value);
        }
    }

    /**
     * A Token that represents an operator in an expression.
     */
    public static class Operator extends Token {
        public Operator(String value) {
            super(value);
        }
    }

    /**
     * A token that matches anything.
     */
    public static final Token MATCH_ANY = new Token() {
        @Override
        public Predicate<Token> getPredicate() {
            return (t) -> true;
        }

        @Override
        public String getValue() {
            return "Token.MATCH_ANY";
        }

        @Override
        public String toString() {
            return "Token.MATCH_ANY";
        }
    };

    /**
     * A token that matches the standard an identifier using the standard Java rules.
     */
    public static class Identifier extends Token {
        private static final Pattern instancePattern = Pattern.compile("\\p{Alpha}\\w*");
        public static final Identifier INSTANCE = new Identifier("Identifier.INSTANCE", (t) -> instancePattern.matcher(t.value).matches());

        private Identifier(String v, Predicate<Token> predicate) {
            super(v, predicate);
        }

        public Identifier(String v) {
            super(v);
        }

        public String toString() {
            if (value == null)
                return "Identifier";
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }
    }

    /**
     * A Token that represents a quoted character strinng.
     */
    public static class CharString extends Token {
        private final static Pattern instancePattern = Pattern.compile("((?<![\\\\])[\'\"])((?:.(?!(?<![\\\\])\\1))*.?)\\1");
        public final static Token INSTANCE = new CharString("CharString.INSTNANCE", (t) -> instancePattern.matcher(t.value).matches());

        private CharString(String v, Predicate<Token> predicate) {
            super(v, predicate);
        }

        public CharString(String v) {
            super(v);
        }

        public CharString() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }
    }

    /**
     * A Token that represents a boolean literal.
     */
    public static class BooleanString extends Token {
        final public static Pattern instancePattern = Pattern.compile("true|false");
        final public static Token INSTANCE = new BooleanString("BooleanString.INSTANCE", (t) -> instancePattern.matcher(t.value).matches());

        public BooleanString(String value) {
            super(value);
        }
        
        public BooleanString(String v, Predicate<Token> predicate) {
            super(v, predicate);
        }
    }

    /**
     * A Token that represents an integer or real number (but <i>not</i> a number in scientific notation).
     */
    public static class NumericString extends Token {
        final private static Pattern instancePattern = Pattern.compile("\\p{Digit}+(?:\\.\\p{Digit}+)?");
        final public static Token INSTANCE = new NumericString("NumnericString.INSTANCE", (t) -> instancePattern.matcher(t.value).matches());
        final private static Pattern anyIntegerPattern = Pattern.compile("\\p{Digit}+");
        final public static Token ANY_INTEGER = new NumericString("NumericString.ANY_INTEGER", (t) -> anyIntegerPattern.matcher(t.value).matches());
        private NumericString(String v, Predicate<Token> predicate) {
            super(v, predicate);
        }

        public NumericString(String v) {
            super(v);
        }

        public NumericString() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }
    }

    static private class NegatedToken extends Token {
        private NegatedToken(Token token) {
            this.value = "negated("+token.value+")";
            this.predicate = token.predicate.negate();
        }
    }

    static public Token negate(Token token) {
        return new NegatedToken(token);
    }
    
    /**
     * A Token that represents a set of other tokens.
     */
    static public class TokenSet extends Token {
        final private Token[] tokens;
        final private Predicate<Token>[] predicates;

        public TokenSet(Token[] tokens) {
            this.tokens = tokens;
            //noinspection unchecked
            this.predicates = Arrays.stream(tokens).map(Token::getPredicate).toArray(Predicate[]::new);
        }

        @Override
        public Predicate<Token> getPredicate() {
            return (t) -> {
                for (Predicate<Token> p : predicates)
                    if (p.test(t))
                        return true;
                return false;
            };
        }

        public String toString() {
            return Stream.of(tokens).map(Token::getValue).collect(Collectors.joining("|"));
        }

        public static TokenSet of(Token... tokens) {
            return new TokenSet(tokens);
        }
    }

    /**
     * A Token that is represented by a regular expression.
     */
    public static class Regex extends Token {
        Pattern pattern;

        static public Token of(String s) {
           return new Regex(s);
        }

        private Regex(String v) {
            this.value = "Regex: " + v;
            pattern = Pattern.compile(v);
            predicate = (t) -> pattern.matcher(t.value).matches();
        }

        @Override
        public boolean equals(Object o) {
            return pattern.matcher(o.toString()).matches();
        }
    }

    static class TokenFinder {
        Set<String> keywordSet;
        Set<String> charSet;
        Set<String> operatorSet;

        protected TokenFinder() {
            this.keywordSet = ImmutableSet.of();
            this.charSet = ImmutableSet.of();
            this.operatorSet = ImmutableSet.of();
        }

        protected TokenFinder(Set<String> keywordSet, Set<String> charSet, Set<String> operatorSet) {
            this.keywordSet = keywordSet;
            this.charSet = charSet;
            this.operatorSet = operatorSet;
        }

        public Token findToken(String s) {
            if (keywordSet.contains(s.toUpperCase()))
                return new Token.Keyword(s.toUpperCase());
            if (operatorSet.contains(s))
                return new Token.Operator(s.toUpperCase());
            if (charSet.contains(s))
                return new Token.Char(s.toUpperCase());
            if (NumericString.instancePattern.matcher(s).matches())
                return new NumericString(s);
            if (s.length() >= 2 && (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'' ||
                    s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"'))
                return new CharString(s);
            return new Identifier(s);
        }
    }

    public static TokenFinder defaultTokenFinder = new TokenFinder();

    public static TokenFinder createTokenFinderFromClass(Class<? extends Token> thisClass) {
        Field[] fields = thisClass.getDeclaredFields();
        ImmutableSet.Builder<String> charBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<String> keywordBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<String> operatorBuilder = ImmutableSet.builder();
        for (Field f : fields) {
            f.setAccessible(true);
            Object o;
            try {
                o = f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (o instanceof Char) {
                Char aChar = (Char) o;
                charBuilder.add(aChar.getValue());
            } else if (o instanceof Keyword) {
                Keyword keyword = (Keyword) o;
                keywordBuilder.add(keyword.getValue());
            } else if (o instanceof Operator) {
                Operator operator = (Operator) o;
                operatorBuilder.add(operator.getValue());
            }
        }
        return new TokenFinder(keywordBuilder.build(), charBuilder.build(), operatorBuilder.build());
    }

    protected static void init(Class<?> thisClass) {

    }
}
