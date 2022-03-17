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
import net.remgant.tools.parser.Parser;
import net.remgant.tools.parser.ParserResult;
import net.remgant.tools.parser.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.remgant.tools.parser.test.SampleSQLParser.SQLToken.*;

public class SampleSQLParser extends Parser {
    public static class SQLToken extends Token {
     @KeywordToken
     final public static Token SELECT =  new Keyword("SELECT");
     @KeywordToken
     final public static Token FROM =  new Keyword("FROM");
     @CharToken
     final public static Token STAR = new Char("*");
     @CharToken
     final public static Token COMMA = new Char(",");

        static {
            init(SQLToken.class);
        }
    }

    static class SelectCommand extends ParserResult {
        boolean projectAll;
        List<String> projectTerms = new ArrayList<>();
        String fromTable;

        public void setProjectAll(boolean projectAll) {
            this.projectAll = projectAll;
        }

        public void addProjectTerm(String projectTerm) {
            projectTerms.add(projectTerm);
        }

        public void addFromTable(String fromTable) {
            this.fromTable = fromTable;
        }

        @Override
        public String toString() {
            return "SelectCommand{" +
                    "projectAll=" + projectAll +
                    ", projectTerms=" + projectTerms +
                    ", fromTable='" + fromTable + '\'' +
                    '}';
        }
    }

    SampleSQLParser() {
        super(ImmutableSet.of('*', ','));
        init();
    }

    @Override
    protected void init() {
        // State 0
        addState(0, SELECT, 1, (s, c) -> new SelectCommand());

        // State 1
        addState(1, STAR, 2, (s, c) -> {
            ((SelectCommand) c).setProjectAll(true);
            return c;
        });
        addState(1, Token.Identifier.INSTANCE, 3, (s, c) -> {
            ((SelectCommand) c).addProjectTerm(s);
            return c;
        });

        // State 2
        addState(2, FROM, 7, null);

        // State 3
        addState(3, FROM, 7, null);
        addState(3, Token.Identifier.INSTANCE, 4, (s, c) -> {
            ((SelectCommand) c).addProjectTerm(s);
            return c;
        });
        addState(3, COMMA, 1, null);

        // State 4
        addState(4, Identifier.INSTANCE, 6, (s, c) -> {
            ((SelectCommand) c).addProjectTerm(s);
            return c;
        });
        addState(4, STAR, 6, (s, c) -> {
            ((SelectCommand) c).setProjectAll(true);
            return c;
        });

        // State 5
        addState(5, COMMA, 1, null);

        // State 6
        addState(6, FROM, 7, null);
        addState(6, Identifier.INSTANCE, 5, null);
        addState(6, COMMA, 1, null);

        // State 7
        addState(7, Identifier.INSTANCE, 8, (s, c) -> {
            ((SelectCommand) c).addFromTable(s);
            return c;
        });
    }

    public static void main(String[] args) {
        new SampleSQLParser().printStateDiagram(System.out);
    }
}
