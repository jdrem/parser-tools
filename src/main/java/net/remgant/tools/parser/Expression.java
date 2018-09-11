package net.remgant.tools.parser;

public class Expression extends ParserResult {
    Token value;
    Expression left;
    Expression right;

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
        StringBuilder sb = new StringBuilder();
        inOrder(sb, this);
        if (sb.length() > 0)
            sb.setLength(sb.length()-1);
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
}
