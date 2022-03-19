package net.remgant.tools.parser;

public class NoMatchForTokenException extends ParserException {
    public NoMatchForTokenException(String token, int state) {
        super(String.format("No match for token=%s in state=%d", token, state));
    }
}
