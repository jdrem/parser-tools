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

import net.remgant.tools.parser.ParserException;
import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleSQLParserTest {
    @Test
    public void testSelectStatement() throws ParserException {
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
