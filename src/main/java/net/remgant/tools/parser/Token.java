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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Token {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Deprecated
    protected @interface KeywordToken {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Deprecated
    protected @interface CharToken {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Deprecated
    protected @interface OperatorToken {
    }

    public static final Predicate<Token> MATCH_ANY = (t) -> true;

    protected static Map<Predicate<Token>, String> tokenMap = new HashMap<>();

    static {
        tokenMap.put(MATCH_ANY, "MATCH_ANY");
    }

    String value;
    Predicate<Token> predicate;

    public String getValue() {
        return value;
    }

    public Predicate<Token> getPredicate() {
        return predicate;
    }

    Token(String v) {
        this.value = v;
        this.predicate = t -> t.getValue().equalsIgnoreCase(v);
    }

    protected Token() {

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

    public static class Keyword extends Token {
        static Set<String> set;

        public Keyword(String value) {
            super(value);
        }
    }

    public static class Char extends Token {
        static Set<String> set;

        public Char(String v) {
            super(v);
        }
    }

    public static class Operator extends Token {
        static Set<String> set;

        public Operator(String v) {
            super(v);
        }
    }


    public static class Identifier extends Token {
        private static final Pattern instancePattern = Pattern.compile("\\p{Alpha}\\p{Alnum}*");
        public static final Predicate<Token> INSTANCE = (t) -> instancePattern.matcher(t.value).matches();

        static {
            tokenMap.put(INSTANCE, "Identifier.INSTANCE");
        }

        public Identifier(String v) {
            super(v);
        }

        public String toString() {
            if (value == null)
                return "Identifier";
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }

    public static class CharString extends Token {
        private final static Pattern instancePattern = Pattern.compile("((?<![\\\\])[\'\"])((?:.(?!(?<![\\\\])\\1))*.?)\\1");
        public final static Predicate<Token> INSTANCE = (t) -> instancePattern.matcher(t.value).matches();

        static {
            tokenMap.put(INSTANCE, "CharString.INSTANCE");
        }

        public CharString(String v) {
            super(v);
        }

        public CharString() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }

    public static class NumericString extends Token {
        final private static Pattern instancePattern = Pattern.compile("\\p{Digit}+(?:\\.\\p{Digit}+)?");
        final private static Pattern anyIntegerPattern = Pattern.compile("\\p{Digit}+");
        final public static Predicate<Token> INSTANCE = (t) -> instancePattern.matcher(t.value).matches();
        final public static Predicate<Token> ANY_INTEGER = (t) -> anyIntegerPattern.matcher(t.value).matches();

        static {
            tokenMap.put(INSTANCE, "NumericString.INSTANCE");
            tokenMap.put(ANY_INTEGER, "NumericString.ANY_INTEGER");
        }

        public NumericString(String v) {
            super(v);
        }

        public NumericString() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }

    public static class Regex extends Token {
        Pattern pattern;

        static public Predicate<Token> of(String s) {
            Pattern p = Pattern.compile(s);
            Predicate<Token> predicate = (t) -> p.matcher(t.value).matches();
            tokenMap.put(predicate, "Regex: " + s);
            return predicate;
        }

        public Regex(String v) {
            super(v);
            pattern = Pattern.compile(v);
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

    @Deprecated
    public static Token findToken(String s) {
        if (Keyword.set.contains(s.toUpperCase()))
            return new Token.Keyword(s.toUpperCase());
        if (Operator.set.contains(s))
            return new Token.Operator(s.toUpperCase());
        if (Char.set.contains(s))
            return new Token.Char(s.toUpperCase());
        if (NumericString.instancePattern.matcher(s).matches())
            return new NumericString(s);
        if (s.length() >= 2 && (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'' ||
                s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"'))
            return new CharString(s);
        return new Identifier(s);
    }

    static public class TokenSet implements Predicate<Token> {
        Predicate<Token> predicates[];

        public TokenSet(Predicate<Token>[] predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(Token token) {
            for (Predicate<Token> p : predicates)
                if (p.test(token))
                    return true;
            return false;
        }

        public String toString() {
            return Stream.of(predicates)
                    .map(c -> {
                        if (c.getClass().getName().toString().contains("Identifier"))
                            return "Identifier";
                        if (c.getClass().getName().toString().contains("NumericString"))
                            return "NumericString";
                        return tokenMap.get(c);
                    })
                    .collect(Collectors.joining("|"));
        }

        @SafeVarargs
        public static TokenSet of(Predicate<Token>... tokens) {
            return new TokenSet(tokens);
        }
    }

    public static Predicate<Token> negate(Predicate<Token> predicate) {
        Predicate<Token> negated = predicate.negate();
        tokenMap.put(negated, "negated(" + tokenMap.get(predicate) + ")");
        return negated;
    }

    static {
        Token.Char.set = ImmutableSet.of();
        Token.Keyword.set = ImmutableSet.of();
        Token.Operator.set = ImmutableSet.of();
    }

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
                tokenMap.put(aChar.getPredicate(), aChar.getValue());
                charBuilder.add(aChar.getValue());
            } else if (o instanceof Keyword) {
                Keyword keyword = (Keyword) o;
                tokenMap.put(keyword.getPredicate(), keyword.getValue());
                keywordBuilder.add(keyword.getValue());
            } else if (o instanceof Operator) {
                Operator operator = (Operator) o;
                tokenMap.put(operator.getPredicate(), operator.getValue());
                operatorBuilder.add(operator.getValue());
            }
        }
        return new TokenFinder(keywordBuilder.build(), charBuilder.build(), operatorBuilder.build());
    }

    protected static void init(Class<?> thisClass) {
        Field[] fields = thisClass.getDeclaredFields();
        ImmutableSet.Builder<String> charBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<String> keywordBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<String> operatorBuilder = ImmutableSet.builder();
        for (Field f : fields) {
            Object o;
            try {
                o = f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (o instanceof Char) {
                Char aChar = (Char) o;
                tokenMap.put(aChar.getPredicate(), aChar.getValue());
                charBuilder.add(aChar.getValue());
            } else if (o instanceof Keyword) {
                Keyword keyword = (Keyword) o;
                tokenMap.put(keyword.getPredicate(), keyword.getValue());
                keywordBuilder.add(keyword.getValue());
            } else if (o instanceof Operator) {
                Operator operator = (Operator) o;
                tokenMap.put(operator.getPredicate(), operator.getValue());
                operatorBuilder.add(operator.getValue());
            }
        }
    }
}
