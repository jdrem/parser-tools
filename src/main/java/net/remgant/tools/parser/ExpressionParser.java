package net.remgant.tools.parser;

import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

public class ExpressionParser {

    Stack<Expression> expressionStack;
    private Set<Predicate<Token>> endTokens;
    private Set<String> logicalOps;
    private Set<String> relationOps;
    private Set<String> additionOps;
    private Set<String> multiplicationOps;

    public ExpressionParser(Set<String> logicalOps, Set<String> relationOps, Set<String> additionOps, Set<String> multiplicationOps) {
        this.logicalOps = logicalOps;
        this.relationOps = relationOps;
        this.additionOps = additionOps;
        this.multiplicationOps = multiplicationOps;
    }

    public ParserResult parse(ListIterator<Token> source, Set<Predicate<Token>> endTokens) throws Exception {
        this.endTokens = endTokens;
        expressionStack = new Stack<>();
        expression(source);
        return expressionStack.pop();
    }

    private boolean match(Token token) {
        return endTokens.stream().anyMatch(p -> p.test(token));
    }

    private void expression(ListIterator<Token> source) {
        logicalExpression(source);
        if (!source.hasNext())
            return;
        Token next = source.next();
        if (match(next)) {
            source.previous();
            return;
        }
        while (logicalOps.contains(next.getValue())) {
            expressionStack.push(new Expression(next));
            logicalExpression(source);
            Expression right = expressionStack.pop();
            Expression opertor = expressionStack.pop();
            Expression left = expressionStack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            expressionStack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void logicalExpression(ListIterator<Token> source) {
        relateion(source);
        if (!source.hasNext())
            return;
        Token next = source.next();
        if (match(next)) {
            source.previous();
            return;
        }
        while (relationOps.contains(next.getValue())) {
            expressionStack.push(new Expression(next));
            relateion(source);
            Expression right = expressionStack.pop();
            Expression opertor = expressionStack.pop();
            Expression left = expressionStack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            expressionStack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void relateion(ListIterator<Token> source) {
        term(source);
        if (!source.hasNext())
            return;
        Token next = source.next();
        if (match(next)) {
            source.previous();
            return;
        }
        while (additionOps.contains(next.getValue())) {
            expressionStack.push(new Expression(next));
            term(source);
            Expression right = expressionStack.pop();
            Expression opertor = expressionStack.pop();
            Expression left = expressionStack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            expressionStack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void term(ListIterator<Token> source) {
        factor(source);
        if (!source.hasNext())
            return;
        Token next = source.next();
        if (match(next)) {
            source.previous();
            return;
        }
        while (multiplicationOps.contains(next.getValue())) {
            expressionStack.push(new Expression(next));
            factor(source);
            Expression right = expressionStack.pop();
            Expression opertor = expressionStack.pop();
            Expression left = expressionStack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            expressionStack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void factor(ListIterator<Token> soruce) {
        Token next = soruce.next();
        if (!(next.getValue().equals("(") || next.getValue().equals(")"))) {
            expressionStack.push(new Expression(next));
            return;
        }
        if (next.getValue().equals("(")) {
            expression(soruce);
            next = soruce.next();
            if (!next.getValue().equals(")"))
                throw new RuntimeException("parse error: expected ')'");
            return;
        }
        throw new RuntimeException("parse error");
    }

}
