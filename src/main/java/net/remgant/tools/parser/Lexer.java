/*
   Copyright 2019 Jeffrey D. Remillard

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package net.remgant.tools.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class Lexer {
    private final Set<Character> specialChars;
    private final Token.TokenFinder tokenFinder;

    protected List<Token> list;
    public final static Set<Character> defaultSpecialChars = ImmutableSet.of(',', '.', '(', ')', '?', '{', '}');

    public Lexer() {
        this(defaultSpecialChars, Token.defaultTokenFinder);
    }

    public Lexer(Set<Character> specialChars) {
        this(specialChars, Token.defaultTokenFinder);
    }

    public Lexer(Token.TokenFinder tokenFinder) {
        this(defaultSpecialChars, tokenFinder);
    }

    public Lexer(Set<Character> specialChars, Token.TokenFinder tokenFinder) {
        this.specialChars = specialChars;
        this.tokenFinder = tokenFinder;
    }

    public void pushBack(Token t) {
        list.add(0, t);
    }

    public ListIterator<Token> tokenize(final String source) {
        ImmutableList.Builder<Token> builder = ImmutableList.builder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inNumber = false;
        StringBuilder sb = new StringBuilder();
        try (PushbackReader in = new PushbackReader(new StringReader(source))) {
            int i;
            while ((i = in.read()) != -1) {
                char c = (char) i;
                if (!inSingleQuote && !inDoubleQuote && !inNumber && Character.isWhitespace(c)) {
                    if (sb.length() > 0) {
                        builder.add(tokenFinder.findToken(sb.toString()));
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
                    builder.add(tokenFinder.findToken(sb.toString()));
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
                    builder.add(tokenFinder.findToken(sb.toString()));
                    sb.setLength(0);
                    inDoubleQuote = false;
                    continue;
                }
                if (!inNumber && Character.isDigit(c)) {
                    if (sb.length() > 0)
                        builder.add(tokenFinder.findToken(sb.toString()));
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
                    builder.add(tokenFinder.findToken(sb.toString()));
                    sb.setLength(0);
                    inNumber = false;
                    in.unread(c);
                    continue;
                }
                if (specialChars.contains(c)) {
                    if (sb.length() > 0)
                        builder.add(tokenFinder.findToken(sb.toString()));
                    builder.add(tokenFinder.findToken(Character.toString(c)));
                    sb.setLength(0);
                    continue;
                }
                sb.append(c);

            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        if (sb.length() > 0)
            builder.add(tokenFinder.findToken(sb.toString()));
        return builder.build().listIterator();
    }

    public ListIterator<Token> tokenize(Iterable<String> strings) {
        ImmutableList.Builder<Token> builder = ImmutableList.builder();
        strings.forEach(s -> builder.add(tokenFinder.findToken(s)));
        return builder.build().listIterator();
    }
}
