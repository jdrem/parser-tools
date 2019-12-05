package net.remgant.tools.parser;

public class NoStateForTokenException extends ParserException {
    public NoStateForTokenException(String token) {
        super(String.format("no next state for token %s", token));
    }
}
