package net.remgant.tools.parser;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lexer {
     private Set<Character> specialChars;

     public Lexer() {
          specialChars = ImmutableSet.of(',', '.', '(', ')', '?', '{', '}');
     }

     public Lexer(Set<Character> specialChars) {
          this.specialChars = specialChars;
     }

     public List<Token> tokenize(final String source) throws IOException {
        List<Token> list = new ArrayList<>();
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
        return list;
    }
}
