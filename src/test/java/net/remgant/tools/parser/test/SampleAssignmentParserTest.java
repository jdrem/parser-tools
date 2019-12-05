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

import net.remgant.tools.parser.NoMatchForTokenException;
import net.remgant.tools.parser.ParserException;
import net.remgant.tools.parser.ParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleAssignmentParserTest {

    @Test
    public void testSingleInt() throws ParserException {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = 1;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=1}", c.toString());
    }

    @Test
    public void testSimpleExpression() throws ParserException {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + c;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c +}", c.toString());
    }

    @Test
    public void testExpressionThreeArgs() throws ParserException {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + c + d;");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c + d +}", c.toString());

    }

    @Test
    public void testExpressionWithParen() throws ParserException {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + (c + d);");
        System.out.println(c);
        assertEquals("AssignmentStatement{target='a', expression=b c d + +}", c.toString());
    }

    @Test(expected = NoMatchForTokenException.class)
    public void testSyntaxError() throws ParserException {
        SampleAssignmentParser parser = new SampleAssignmentParser();
        ParserResult c = parser.parse("a = b + + c;");
    }
}
