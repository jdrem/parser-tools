package net.remgant.tools.parser.test;

@SuppressWarnings("unused")
public class Expression {
    Expression left;
    String value;
    Expression right;

    @SuppressWarnings("WeakerAccess")
    public Expression(String value) {
        this.value = value;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }
}
