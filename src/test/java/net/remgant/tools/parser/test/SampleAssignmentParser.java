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
import net.remgant.tools.parser.*;

import java.util.function.Predicate;

import static net.remgant.tools.parser.test.SampleAssignmentParser.SampleToken.EQUALS;
import static net.remgant.tools.parser.test.SampleAssignmentParser.SampleToken.SEMI_COLON;

public class SampleAssignmentParser extends Parser {

    public static class SampleToken extends Token {
        @CharToken
        final public static Token EQUALS = new Char("=");
        @CharToken
        final public static Token SEMI_COLON = new Char(";");
        @OperatorToken
        final public static Token PLUS_SIGN = new Operator("+");

        static {
            init(SampleToken.class);
        }
    }

    private ExpressionParser expressionParser = new ExpressionParser(ImmutableSet.of("and", "or"),
            ImmutableSet.of("<", ">", "<=", ">=", "==", "!="),
            ImmutableSet.of("+", "-"),
            ImmutableSet.of("*", "/", "%"));

    @Override
    protected void init() {
        addState(0, Token.Identifier.INSTANCE, 1, (s, c) -> new AssignmentStatement(s));

        addState(1, EQUALS, 2, (s, c) -> {
            AssignmentStatement as = (AssignmentStatement) c;
            ParserResult result = null;
            try {
                result = expressionParser.parse(lexer, ImmutableSet.of(SEMI_COLON), "");
            } catch (Exception e) {
                as.setError(e.getCause());
            }
            as.setExpression((net.remgant.tools.parser.Expression) result);
            return as;
        });

        addState(2, SEMI_COLON, 2, null);
    }


    public SampleAssignmentParser() {
        lexerSpecialChars = ImmutableSet.of('+', '-', '=', ';', '(', ')');
    }

    private static class AssignmentStatement extends ParserResult {
        private final String target;
        private Expression expression;
        Throwable error;

        public AssignmentStatement(String target) {
            this.target = target;
        }

        public void setExpression(Expression expression) {
            this.expression = expression;
        }

        public void setError(Throwable cause) {
            this.error = cause;
        }

        @Override
        public String toString() {
            return "AssignmentStatement{" +
                    "target='" + target + '\'' +
                    ", expression=" + expression +
                    '}';
        }
    }
}
