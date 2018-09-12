package net.remgant.tools.parser.test;

import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleAssignmentParserTest {

    @Test
    public void testSingleInt() throws Exception {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = 1;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=1}", c.toString());
    }

    @Test
    public void testSimpleExpression() throws Exception {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + c;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c +}", c.toString());
    }

    @Test
    public void testExpressionThreeArgs() throws Exception {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + c + d;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c + d +}", c.toString());

    }

    @Test
    public void testExpressionWithParen() throws Exception {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + (c + d);");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c d + +}", c.toString());
    }
}
