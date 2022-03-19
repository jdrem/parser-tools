package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableSet;
import net.remgant.tools.parser.*;

import java.util.Collections;
import java.util.function.Predicate;

import static net.remgant.tools.parser.test.ExpressionBuilderSampleParser.SampleToken.*;

public class ExpressionBuilderSampleParser extends Parser {
    public static class SampleToken extends Token {
        final public static Token EQUALS = new Token.Char("=");
        final public static Token SEMI_COLON = new Token.Char(";");
        final public static Token LEFT_PAREN = new Token.Char("(");
        final public static Token RIGHT_PAREN = new Token.Char(")");
        final public static Token PLUS_SIGN = new Token.Char("+");
        final public static Token MINUS_SIGN = new Token.Char("-");
        final public static Token STAR = new Token.Char("*");
        final public static Token SLASH = new Token.Char("/");

        static {
            init(SampleAssignmentParser.SampleToken.class);
        }
    }

    private final ExpressionParser expressionParser = new ExpressionParser(ImmutableSet.of("and", "or"),
            ImmutableSet.of("<", ">", "<=", ">=", "==", "!="),
            ImmutableSet.of("+", "-"),
            ImmutableSet.of("*", "/", "%"));

    @Override
    protected void init() {
        addState(0, Token.Identifier.INSTANCE, 1, (s, c) -> new AssignmentStatement(s));

        addState(1, EQUALS, 2, null);

        ParserAction pa = (s, c) -> {
            ((AssignmentStatement) c).expression.add(s);
            return c;
        };

        addState(2, Token.Identifier.INSTANCE, 3, pa);
        addState(2, Token.NumericString.INSTANCE, 3, pa);
        addState(2, LEFT_PAREN, 2, pa);
        addState(3, Token.TokenSet.of(PLUS_SIGN, MINUS_SIGN, STAR, SLASH), 2, pa);
        addState(3, RIGHT_PAREN, 3, pa);
        addState(3, LEFT_PAREN, 4, null);
        addState(3, SEMI_COLON, 999, (s, c) -> {
            AssignmentStatement assignmentStatement = (AssignmentStatement)c;
            assignmentStatement.node = assignmentStatement.expression.evaluate();
            return c;
        });

        terminalStates = Collections.singleton(999);

    }


    public ExpressionBuilderSampleParser() {
        super(ImmutableSet.of('+', '-', '=', ';', '(', ')'));
    }

     static class AssignmentStatement extends ParserResult {
        private final String target;
        private ExpressionBuilder<String> expression;
        private ExpressionBuilder.Node<String> node;
        Throwable error;


        public AssignmentStatement(String target) {
            this.target = target;
            this.expression = new ExpressionBuilder<>(ImmutableSet.of("and", "or"),
                        ImmutableSet.of("<", ">", "<=", ">=", "==", "!="),
                        ImmutableSet.of("+", "-"),
                        ImmutableSet.of("*", "/", "%"),"(", ")");
        }

        public void setExpression(ExpressionBuilder expression) {
            this.expression = expression;
        }

        public void setError(Throwable cause) {
            this.error = cause;
        }

         public String getTarget() {
             return target;
         }

         public ExpressionBuilder.Node<String> getNode() {
             return node;
         }

         @Override
        public String toString() {
            return "AssignmentStatement{" +
                    "target='" + target + '\'' +
                    ", expression=" + expression +
                    ", node=" + node +
                    '}';
        }
    }
}
