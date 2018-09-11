package net.remgant.tools.parser;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class Lexer implements ListIterator<Token> {
    private Set<Character> specialChars;
    protected List<Token> list;

    public Lexer(String source) {
        this(ImmutableSet.of(',', '.', '(', ')', '?', '{', '}'), source);
    }

    public Lexer(Set<Character> specialChars, String source) {
        this.specialChars = specialChars;
        try {
            tokenize(source);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ListIterator<Token> listIterator;

    @Override
    public boolean hasNext() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.hasNext();
    }

    @Override
    public Token next() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.next();
    }

    @Override
    public boolean hasPrevious() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.hasPrevious();
    }

    @Override
    public Token previous() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.previous();
    }

    @Override
    public int nextIndex() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        if (listIterator == null)
            listIterator = list.listIterator();
        return listIterator.previousIndex();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Token token) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void add(Token token) {
        throw new UnsupportedOperationException();
    }

    public Token nextToken() {
        if (list.size() == 0)
            return null;
        return list.remove(0);
    }

    public void pushBack(Token t) {
        list.add(0, t);
    }

    private void tokenize(final String source) throws IOException {
        list = new ArrayList<>();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inNumber = false;
        PushbackReader in = new PushbackReader(new StringReader(source));
        int i;
        StringBuilder sb = new StringBuilder();
        while ((i = in.read()) != -1) {
            char c = (char) i;
            if (!inSingleQuote && !inDoubleQuote && !inNumber && Character.isWhitespace(c)) {
                if (sb.length() > 0) {
                    list.add(Token.findToken(sb.toString()));
                    sb.setLength(0);
                }
                continue;
            }
            if (c == '\'' && !inSingleQuote && !inDoubleQuote) {
                inSingleQuote = true;
                sb.append(c);
                continue;
            }
            if (c == '\'' && inSingleQuote) {
                sb.append(c);
                list.add(Token.findToken(sb.toString()));
                sb.setLength(0);
                inSingleQuote = false;
                continue;
            }
            if (inSingleQuote && c == '\\') {
                sb.append(c);
                i = in.read();
                sb.append((char) i);
                continue;
            }
            if (inSingleQuote) {
                sb.append(c);
                continue;
            }
            if (inDoubleQuote && c == '\\') {
                sb.append(c);
                i = in.read();
                sb.append((char) i);
                continue;
            }
            if (c == '\"' && !inDoubleQuote) {
                sb.append(c);
                inDoubleQuote = true;
                continue;
            }
            if (c == '\"') {
                sb.append(c);
                list.add(Token.findToken(sb.toString()));
                sb.setLength(0);
                inDoubleQuote = false;
                continue;
            }
            if (!inNumber && Character.isDigit(c)) {
                if (sb.length() > 0)
                    list.add(Token.findToken(sb.toString()));
                inNumber = true;
                sb.setLength(0);
                sb.append(c);
                continue;
            }
            if (inNumber && c == '.') {
                sb.append(c);
                continue;
            }
            if (inNumber && !Character.isDigit(c) && c != '.') {
                list.add(Token.findToken(sb.toString()));
                sb.setLength(0);
                inNumber = false;
                in.unread(c);
                continue;
            }
            if (specialChars.contains(c)) {
                if (sb.length() > 0)
                    list.add(Token.findToken(sb.toString()));
                list.add(Token.findToken(Character.toString(c)));
                sb.setLength(0);
                continue;
            }
            sb.append(c);

        }
        if (sb.length() > 0)
            list.add(Token.findToken(sb.toString()));
    }
}
