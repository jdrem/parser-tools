package net.remgant.tools.parser;

import java.util.*;

public class ExpressionBuilder<V> {
    private Set<V> logicalOps;
    private Set<V> relationOps;
    private Set<V> additionOps;
    private Set<V> multiplicationOps;
    private V leftParen;
    private V rightParen;
    private List<V> list;
    private Stack<Node<V>> stack;

    public static class Node<V> {
        V value;
        Node<V> left;
        Node<V> right;

        public Node(V s) {
            this.value = s;
        }

        public void setLeft(Node<V> node) {
            left = node;
        }

        public void setRight(Node<V> node) {
            right = node;
        }

        private void printPostOrder(StringBuilder sb) {
            if (this.left != null)
                left.printPostOrder(sb);
            if (this.right != null)
                right.printPostOrder(sb);
            sb.append(value);
            sb.append(' ');
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<V> getLeft() {
            return left;
        }

        public Node<V> getRight() {
            return right;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            printPostOrder(sb);
            if (sb.charAt(sb.length() - 1) == ' ')
                sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

        private void inOrder(List<Node<V>> list, Node<V> e) {
            if (e.left != null)
                inOrder(list, e.left);
            if (e.right != null)
                inOrder(list, e.right);
            list.add(e);
        }

        public Iterator<Node<V>> iterator() {
            List<Node<V>> list = new ArrayList<>();
            inOrder(list, this);
            return list.iterator();
        }
    }

    public ExpressionBuilder(Set<V> logicalOps, Set<V> relationOps, Set<V> additionOps, Set<V> multOps, V lparen, V rparen) {
        this.logicalOps = logicalOps;
        this.relationOps = relationOps;
        this.additionOps = additionOps;
        this.multiplicationOps = multOps;
        this.leftParen = lparen;
        this.rightParen = rparen;
        this.list = new ArrayList<>();
    }

    public void add(V value) {
        list.add(value);
    }

    public Node<V> evaluate() {
        stack = new Stack<>();
        expression(list.listIterator());
        return stack.size() > 0 ? stack.pop() : null;
    }

    private void expression(ListIterator<V> source) {
        logicalExpression(source);
        if (!source.hasNext())
            return;
        V next = source.next();
        while (logicalOps.contains(next)) {
            stack.push(new Node<>(next));
            logicalExpression(source);
            Node<V> right = stack.pop();
            Node<V> opertor = stack.pop();
            Node<V> left = stack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            stack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void logicalExpression(ListIterator<V> source) {
        relateion(source);
        if (!source.hasNext())
            return;
        V next = source.next();
        while (relationOps.contains(next)) {
            stack.push(new Node<>(next));
            relateion(source);
            Node<V> right = stack.pop();
            Node<V> opertor = stack.pop();
            Node<V> left = stack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            stack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void relateion(ListIterator<V> source) {
        term(source);
        if (!source.hasNext())
            return;
        V next = source.next();
        while (additionOps.contains(next)) {
            stack.push(new Node<>(next));
            term(source);
            Node<V> right = stack.pop();
            Node<V> opertor = stack.pop();
            Node<V> left = stack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            stack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void term(ListIterator<V> source) {
        factor(source);
        if (!source.hasNext())
            return;
        V next = source.next();
        while (multiplicationOps.contains(next)) {
            stack.push(new Node<>(next));
            factor(source);
            Node<V> right = stack.pop();
            Node<V> opertor = stack.pop();
            Node<V> left = stack.pop();
            opertor.setLeft(left);
            opertor.setRight(right);
            stack.push(opertor);
            if (!source.hasNext())
                return;
            next = source.next();
        }
        source.previous();
    }

    private void factor(ListIterator<V> source) {
        V next = source.next();
        if (!(next.equals(leftParen) || next.equals(rightParen))) {
            stack.push(new Node<>(next));
            return;
        }
        if (next.equals(leftParen)) {
            expression(source);
            next = source.next();
            if (!next.equals(rightParen))
                throw new RuntimeException("parse error: expected ')'");
            return;
        }
        throw new RuntimeException("parse error");
    }
}
