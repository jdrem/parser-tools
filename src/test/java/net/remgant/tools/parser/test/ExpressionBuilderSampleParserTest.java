package net.remgant.tools.parser.test;

import net.remgant.tools.parser.ParserException;
import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpressionBuilderSampleParserTest {
    @Test
    public void testSingleInt() throws ParserException {
        ExpressionBuilderSampleParser parser = new ExpressionBuilderSampleParser();
        ParserResult c = parser.parse("a = 1;");
        System.out.println(c);
        assertTrue(c instanceof ExpressionBuilderSampleParser.AssignmentStatement);
        ExpressionBuilderSampleParser.AssignmentStatement assignmentStatement = (ExpressionBuilderSampleParser.AssignmentStatement) c;
        assertEquals("a", assignmentStatement.getTarget());
        assertEquals("1", assignmentStatement.getNode().toString());
    }

    @Test
    public void testSimpleExpression() throws ParserException {
        ExpressionBuilderSampleParser parser = new ExpressionBuilderSampleParser();
        ParserResult c = parser.parse("a = b + c;");
        System.out.println(c);
        assertTrue(c instanceof ExpressionBuilderSampleParser.AssignmentStatement);
        ExpressionBuilderSampleParser.AssignmentStatement assignmentStatement = (ExpressionBuilderSampleParser.AssignmentStatement) c;
        assertEquals("a", assignmentStatement.getTarget());
        assertEquals("b c +", assignmentStatement.getNode().toString());
    }

    @Test
    public void testExpressionThreeArgs() throws ParserException {
        ExpressionBuilderSampleParser parser = new ExpressionBuilderSampleParser();
        ParserResult c = parser.parse("a = b + c + d;");
        System.out.println(c);
        assertTrue(c instanceof ExpressionBuilderSampleParser.AssignmentStatement);
        ExpressionBuilderSampleParser.AssignmentStatement assignmentStatement = (ExpressionBuilderSampleParser.AssignmentStatement) c;
        assertEquals("a", assignmentStatement.getTarget());
        assertEquals("b c + d +", assignmentStatement.getNode().toString());
    }

    @Test
    public void testExpressionWithParen() throws ParserException {
        ExpressionBuilderSampleParser parser = new ExpressionBuilderSampleParser();
        ParserResult c = parser.parse("a = b + (c + d);");
        System.out.println(c);
        assertTrue(c instanceof ExpressionBuilderSampleParser.AssignmentStatement);
        ExpressionBuilderSampleParser.AssignmentStatement assignmentStatement = (ExpressionBuilderSampleParser.AssignmentStatement) c;
        assertEquals("a", assignmentStatement.getTarget());
        assertEquals("b c d + +", assignmentStatement.getNode().toString());
    }
}
