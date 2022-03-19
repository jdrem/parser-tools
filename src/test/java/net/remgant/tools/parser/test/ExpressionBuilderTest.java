package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableSet;
import net.remgant.tools.parser.ExpressionBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpressionBuilderTest {
    @Test
    public void test1() {
        ExpressionBuilder<String> expressionBuilder = new ExpressionBuilder<>(ImmutableSet.of(), ImmutableSet.of(), ImmutableSet.of("+", "-"), ImmutableSet.of("*", "/"), "(", ")");
        expressionBuilder.add("a");
        expressionBuilder.add("+");
        expressionBuilder.add("b");

        ExpressionBuilder.Node<String> node = expressionBuilder.evaluate();
        assertEquals("a b +", node.toString());
        System.out.println(node);
    }

    @Test
    public void test2() {
        ExpressionBuilder<String> expressionBuilder = new ExpressionBuilder<>(ImmutableSet.of(), ImmutableSet.of(), ImmutableSet.of("+", "-"), ImmutableSet.of("*", "/"), "(", ")");
        expressionBuilder.add("a");
        expressionBuilder.add("*");
        expressionBuilder.add("b");
        expressionBuilder.add("+");
        expressionBuilder.add("c");
        expressionBuilder.add("*");
        expressionBuilder.add("d");

        ExpressionBuilder.Node<String> node = expressionBuilder.evaluate();
        System.out.println(node);
        assertEquals("a b * c d * +", node.toString());
    }

    @Test
    public void test3() {
        ExpressionBuilder<String> expressionBuilder = new ExpressionBuilder<>(ImmutableSet.of(), ImmutableSet.of(), ImmutableSet.of("+", "-"), ImmutableSet.of("*", "/"), "(", ")");
        for (String s : "a * b + c * d".split(" ")) {
            expressionBuilder.add(s);
        }
        ExpressionBuilder.Node<String> node = expressionBuilder.evaluate();
        System.out.println(node);
        assertEquals("a b * c d * +", node.toString());
    }

    @Test
    public void test4() {
        ExpressionBuilder<String> expressionBuilder = new ExpressionBuilder<>(ImmutableSet.of(), ImmutableSet.of(), ImmutableSet.of("+", "-"), ImmutableSet.of("*", "/"), "(", ")");
        for (String s : "( a + b ) * ( c + d )".split(" ")) {
            expressionBuilder.add(s);
        }
        ExpressionBuilder.Node<String> node = expressionBuilder.evaluate();
        System.out.println(node);
        assertEquals("a b + c d + *", node.toString());
    }

    @Test
    public void test5() {
        ExpressionBuilder<String> expressionBuilder = new ExpressionBuilder<>(ImmutableSet.of(), ImmutableSet.of(), ImmutableSet.of("+", "-"), ImmutableSet.of("*", "/"), "(", ")");
        for (String s : "a * b * c".split(" ")) {
            expressionBuilder.add(s);
        }
        ExpressionBuilder.Node<String> node = expressionBuilder.evaluate();
        System.out.println(node);
        assertEquals("a b * c *", node.toString());
    }
}
