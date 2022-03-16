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
package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableSet;
import net.remgant.tools.parser.ExpressionParser;
import net.remgant.tools.parser.Lexer;
import net.remgant.tools.parser.ParserResult;
import net.remgant.tools.parser.Token;
import org.junit.Before;
import org.junit.Test;

import java.util.ListIterator;
import java.util.function.Predicate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExpressionParserTest {
    static public class TestToken extends Token {
        @CharToken
        final public static Predicate<Token> SEMI_COLON = (t) -> t.getValue().equals(";");
        @CharToken
        final public static Predicate<Token> LEFT_PAREN = (t) -> t.getValue().equals("(");
        @CharToken
        final public static Predicate<Token> RIGHT_PAREN = (t) -> t.getValue().equals(")");

        static {
            init(net.remgant.tools.parser.test.ExpressionParserTest.TestToken.class);
        }
    }

    private ExpressionParser expressionParser;

    @Before
    public void setup() {
        expressionParser = new ExpressionParser(ImmutableSet.of("and", "or"),
                ImmutableSet.of("<", ">", "<=", ">=", "==", "!="),
                ImmutableSet.of("+", "-"),
                ImmutableSet.of("*", "/", "%"));

    }

    @Test
    public void testAddition() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a + b;");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b +", result.toString());
        assertTrue(tokenIterator.hasNext());
        assertEquals(";", tokenIterator.next().getValue());
    }

    @Test
    public void testEndChar() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a + b");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b +", result.toString());
        assertFalse(tokenIterator.hasNext());
    }

    @Test
    public void testThreeTermAddition() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a + b + c");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b + c +", result.toString());
        assertFalse(tokenIterator.hasNext());
    }

    @Test
    public void testFourTermMultAdd() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a * b + c * d)");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b * c d * +", result.toString());
        assertTrue(tokenIterator.hasNext());
        assertEquals(")", tokenIterator.next().getValue());
    }

    @Test
    public void testWithParen() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a * (b * c)");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("a b c * *", result.toString());
        assertFalse(tokenIterator.hasNext());
    }

    @Test
    public void testAllLevels() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize("a * b + c * d > e * f  + g * h and i * j <= k + l");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("a b * c d * + e f * g h * + > i j * k l + <= and", result.toString());
        assertFalse(tokenIterator.hasNext());
    }

    @Test
    public void testEmptyExpression() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'));
        ListIterator<Token> tokenIterator = lexer.tokenize(";");
        ParserResult result = expressionParser.parse(tokenIterator, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("", result.toString());
        assertTrue(tokenIterator.hasNext());
    }
}
