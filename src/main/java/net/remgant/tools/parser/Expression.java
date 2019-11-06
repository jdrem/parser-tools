package net.remgant.tools.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Expression extends ParserResult {
    protected Token value;
    protected Expression left;
    protected Expression right;

    public Expression() {

    }

    public Expression(Token value) {
        this.value = value;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        if (value == null)
            return "";
        StringBuilder sb = new StringBuilder();
        inOrder(sb, this);
        if (sb.length() > 0)
            sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private void inOrder(StringBuilder sb, Expression e) {
        if (e.left != null)
            inOrder(sb, e.left);
        if (e.right != null)
            inOrder(sb, e.right);
        sb.append(e.value);
        sb.append(" ");
    }

    private List<Token> inOrderList() {
        List<Token> list = new ArrayList<>();
        inOrderList(list, this);
        return list;
    }

    private void inOrderList(List<Token> list, Expression e) {
        if (e.left != null)
            inOrderList(list, e.left);
        if (e.right != null)
            inOrderList(list, e.right);
        list.add(e.value);

    }

    private List<Expression> inOrder() {
        List<Expression> list = new ArrayList<>();
        inOrder(list, this);
        return list;
    }

    private void inOrder(List<Expression> list, Expression e) {
       if (e.left != null)
           inOrder(list, e.left);
       if (e.right != null)
           inOrder(list, e.right);
       list.add(e);
    }

    public Stream<Token> stream() {
        return inOrderList().stream();
    }

    public Iterator<Token> tokenIterator() {
        return inOrderList().iterator();
    }

    public Iterator<Expression> iterator() {
         return inOrder().iterator();
    }
}
