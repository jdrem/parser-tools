package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableSet;
import net.remgant.tools.parser.ExpressionParser;
import net.remgant.tools.parser.Lexer;
import net.remgant.tools.parser.ParserResult;
import net.remgant.tools.parser.Token;
import org.junit.Before;
import org.junit.Test;

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
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'), "a + b;");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b +", result.toString());
        assertTrue(lexer.hasNext());
        assertEquals(";", lexer.next().getValue());
    }

    @Test
    public void testEndChar() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'), "a + b");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b +", result.toString());
        assertFalse(lexer.hasNext());
    }

    @Test
    public void testThreeTermAddition() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'), "a + b + c");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b + c +", result.toString());
        assertFalse(lexer.hasNext());
    }

    @Test
    public void testFourTermMultAdd() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', ')'), "a * b + c * d)");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON, TestToken.RIGHT_PAREN));
        System.out.println(result);
        assertEquals("a b * c d * +", result.toString());
        assertTrue(lexer.hasNext());
        assertEquals(")", lexer.next().getValue());
    }

    @Test
    public void testWithParen() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'), "a * (b * c)");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("a b c * *", result.toString());
        assertFalse(lexer.hasNext());
    }

    @Test
    public void testAllLevels() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'), "a * b + c * d > e * f  + g * h and i * j <= k + l");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("a b * c d * + e f * g h * + > i j * k l + <= and", result.toString());
        assertFalse(lexer.hasNext());
    }

    @Test
    public void testEmptyExpression() throws Exception {
        Lexer lexer = new Lexer(ImmutableSet.of(';', '(', ')'), ";");
        ParserResult result = expressionParser.parse(lexer, ImmutableSet.of(TestToken.SEMI_COLON));
        System.out.println(result);
        assertEquals("", result.toString());
        assertTrue(lexer.hasNext());
    }
}
