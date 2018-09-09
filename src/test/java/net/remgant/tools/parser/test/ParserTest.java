package net.remgant.tools.parser.test;

import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    @Test
    public void testSingleInt() throws Exception {
        TestParser parser = new TestParser();
        parser.init();
        ParserResult c = parser.parse("a = 1;");
        System.out.println(c);
        assertEquals("a = 1", c.toString());
    }

    @Test
    public void testSimpleExpression() throws Exception {
        TestParser parser = new TestParser();
        parser.init();
        ParserResult c = parser.parse("a = b + c;");
        System.out.println(c);
        assertEquals("a = b c +", c.toString());

    }

    @Test
    public void testExpressionThreeArgs() throws Exception {
        TestParser parser = new TestParser();
        parser.init();
        ParserResult c = parser.parse("a = b + c + d;");
        System.out.println(c);
        assertEquals("a = b c + d +", c.toString());

    }

    @Test
    public void testExpressionWithParen() throws Exception {
        TestParser parser = new TestParser();
        parser.init();
        ParserResult c = parser.parse("a = b + (c + d);");
        System.out.println(c);
        assertEquals("a = b c d + +", c.toString());
    }
}
