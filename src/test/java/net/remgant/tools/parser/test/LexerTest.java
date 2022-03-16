package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableList;
import net.remgant.tools.parser.Lexer;
import net.remgant.tools.parser.Token;
import org.junit.Test;

import java.util.ListIterator;

import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void testListOfStrings() {
        Lexer lexer = new Lexer();
        ListIterator<Token> tokenIterator = lexer.tokenize(ImmutableList.of("a","=","1"));
        assertEquals(new Token.Identifier("a"), tokenIterator.next());
        assertEquals(new Token.Identifier("="), tokenIterator.next());
        assertEquals(new Token.NumericString("1"), tokenIterator.next());
    }
}
