package net.remgant.tools.parser;

@FunctionalInterface
public interface ParserAction {
    ParserResult doAction(String string, ParserResult parserResult);
}
