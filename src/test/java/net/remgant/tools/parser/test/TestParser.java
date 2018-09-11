package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableSet;
import net.remgant.tools.parser.*;

import java.util.Stack;

public class TestParser extends Parser {

    public class LValue extends ParserResult {
        String id;

        LValue(String id) {
            this.id = id;
        }
        @Override
        public String toString() {
            return id;
        }
    }

    public class AssignmentStatement extends ParserResult {

        LValue lvalue;
        Stack<Expression> expressionStack;
        AssignmentStatement(LValue lValue) {
           this.lvalue = lValue;
           expressionStack = new Stack<>();
        }

        AssignmentStatement addValue(String value) {
            if (expressionStack.size() == 0)
                expressionStack.push(new Expression(value));
            else if (expressionStack.peek().value == null) {
                expressionStack.peek().value = value;
            }
            else if (expressionStack.peek().value.equals("+") || expressionStack.peek().value.equals("-")) {
                Expression op = expressionStack.pop();
                op.left = expressionStack.pop();
                op.right = new Expression(value);
                expressionStack.push(op);
            }
            return this;
        }

        AssignmentStatement addOperator(String value) {
            expressionStack.push(new Expression(value));
            return this;
        }

        AssignmentStatement addSubExpression() {
            expressionStack.push(new Expression(null));
            return this;
        }

        AssignmentStatement resolveSubExpression() {
            Expression right = expressionStack.pop();
            Expression op = expressionStack.pop();
            Expression left = expressionStack.pop();
            op.right = right;
            op.left = left;
            expressionStack.push(op);
            return this;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(lvalue);
            sb.append(" =");
            inOrder(sb,expressionStack.peek());
            return sb.toString();
        }

        private void inOrder(StringBuilder sb, Expression e) {
            if (e.left != null)
                inOrder(sb,e.left);
            if (e.right != null)
                inOrder(sb,e.right);
            sb.append(" ");
            sb.append(e.value);
        }
    }
    TestParser() {
        super();
        lexerSpecialChars = ImmutableSet.of('+','-','=',';','(',')');
    }

    @Override
    protected void init() {
        addState(0, Token.Identifier.INSTANCE, 1, (s,c) -> new AssignmentStatement(new LValue(s)));

        addState(1, TestToken.EQUALS, 2, null);

        addState(2, Token.NumericString.INSTANCE, 3, (s,c) -> ((AssignmentStatement)c).addValue(s));
        addState(2, Token.Identifier.INSTANCE, 3, (s,c) -> ((AssignmentStatement)c).addValue(s));
        addState(2, TestToken.LEFT_PAREN, 2, (s,c) -> ((AssignmentStatement)c).addSubExpression());
        addState(2, TestToken.SEMI_COLON, 4, null);

        addState(3, Token.TokenSet.of(TestToken.PLUS, TestToken.MINUS), 2, (s,c) -> ((AssignmentStatement)c).addOperator(s));
        addState(3, TestToken.RIGHT_PAREN, 2, (s,c) -> ((AssignmentStatement)c).resolveSubExpression());
        addState(3, TestToken.SEMI_COLON, 4, null);

        addState(4, Token.MATCH_ANY, 4, null);

    }

    public static void main(String args[]) {
        new TestParser().printStateDiagram(System.out);
    }
}
