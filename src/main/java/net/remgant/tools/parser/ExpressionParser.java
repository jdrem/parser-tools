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
package net.remgant.tools.parser;

import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        if (expressionStack.empty())
            return new Expression();
        return expressionStack.pop();
    }

    public ParserResult parse(ListIterator<Token> source, Set<Token> endTokens, String s) throws Exception {
          this.endTokens = endTokens.stream().map(t -> t.getPredicate()).collect(Collectors.toSet());
          expressionStack = new Stack<>();
          expression(source);
          if (expressionStack.empty())
              return new Expression();
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
        if (match(next)) {
            soruce.previous();
            return;
        }
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
