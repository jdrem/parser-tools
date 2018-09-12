package net.remgant.tools.parser.test;

import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleSQLParserTest {
    @Test
    public void testSelectStatement() throws Exception {
        SampleSQLParser parser = new SampleSQLParser();
        ParserResult parserResult = parser.parse("select * from employee");
        System.out.println(parserResult);
        assertEquals("SelectCommand{projectAll=true, projectTerms=[], fromTable='employee'}", parserResult.toString());
    }

    @Test
    public void testSelectStatementWithProjectList() throws Exception {
        SampleSQLParser parser = new SampleSQLParser();
        ParserResult parserResult = parser.parse("select id,name from employee");
        System.out.println(parserResult);
        assertEquals("SelectCommand{projectAll=false, projectTerms=[id, name], fromTable='employee'}", parserResult.toString());
    }
}
