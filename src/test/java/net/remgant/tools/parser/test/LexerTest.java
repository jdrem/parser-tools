package net.remgant.tools.parser.test;

import com.google.common.collect.ImmutableList;
import net.remgant.tools.parser.Lexer;
import net.remgant.tools.parser.Token;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void testListOfStrings() {
        Lexer lexer = new Lexer(ImmutableList.of("a","=","1"));
        assertEquals(new Token.Identifier("a"), lexer.next());
        assertEquals(new Token.Identifier("="), lexer.next());
        assertEquals(new Token.NumericString("1"), lexer.next());
    }
}
