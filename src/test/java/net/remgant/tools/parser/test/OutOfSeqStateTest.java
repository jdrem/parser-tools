package net.remgant.tools.parser.test;

import net.remgant.tools.parser.Parser;
import net.remgant.tools.parser.ParserException;
import net.remgant.tools.parser.ParserResult;
import net.remgant.tools.parser.Token;
import org.junit.Test;

import java.util.Collections;

public class OutOfSeqStateTest {
    @Test
    public void testSkipStates() throws ParserException {
        Parser parser = new Parser() {
            @Override
            protected void init() {
                addState(0, Token.MATCH_ANY, 1, null);
                addState(1, Token.MATCH_ANY, 100, null);
                addState(100, Token.MATCH_ANY, 999, null);
                terminalStates = Collections.singleton(999);
            }

            @Override
            public ParserResult parse(String source) throws ParserException {
                init();
                return super.parse(source);
            }
        };
        parser.parse("a b c");
        parser.printStateDiagram(System.out);
    }

    @Test
    public void testOutOfOrderStates() throws ParserException {
        Parser parser = new Parser() {
            @Override
            protected void init() {
                addState(0, Token.MATCH_ANY, 1, null);
                addState(2, Token.MATCH_ANY, 3, null);
                addState(1, Token.MATCH_ANY, 2, null);
                addState(3, Token.MATCH_ANY, 999, null);
                terminalStates = Collections.singleton(999);
            }

            @Override
            public ParserResult parse(String source) throws ParserException {
                init();
                return super.parse(source);
            }
        };

        parser.parse("a b c d");
    }
}
