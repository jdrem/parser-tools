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


import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public abstract class Parser {
    final private Logger log = LoggerFactory.getLogger(Parser.class);

    protected Lexer lexer;
    protected Set<Character> lexerSpecialChars;
    @SuppressWarnings("WeakerAccess")
    protected Set<Integer> terminalStates = ImmutableSet.of();

    static class State {
        int stateNumber;
        Predicate<Token> predicate;
        int nextStateNumber;
        ParserAction action;

        State(int stateNumber, Predicate<Token> predicate, int nextStateNumber, ParserAction action) {
            this.stateNumber = stateNumber;
            this.predicate = predicate;
            this.nextStateNumber = nextStateNumber;
            this.action = action;
        }
    }

    private List<List<State>> stateList;

    protected void addState(int state, Predicate<Token> predicate, int nextState, ParserAction action) {
        if (stateList.size() < state + 1) {
            List<State> list = new ArrayList<>();
            stateList.add(state, list);
        }
        List<State> list = stateList.get(state);
        list.add(new State(state, predicate, nextState, action));
    }

    abstract protected void init();

    protected Parser() {
        stateList = new ArrayList<>();
        init();
    }

    protected void printStateDiagram(@SuppressWarnings("SameParameterValue") PrintStream out) {
        for (List<State> l : stateList) {
            for (State s : l) {
                String ts;
                if (Token.tokenMap.containsKey(s.predicate))
                    ts = Token.tokenMap.get(s.predicate);
                else
                    ts = s.predicate.toString();
                out.printf("%d %s %d%n", s.stateNumber, ts, s.nextStateNumber);
            }
        }
    }

    public ParserResult parse(String source) throws Exception {
//        Lexer lexer;
        if (lexerSpecialChars == null)
            lexer = new Lexer(source);
        else
            lexer = new Lexer(lexerSpecialChars, source);
        int state = 0;
        int r;
        ParserResult parserResult = null;
        Token token;
        while (lexer.hasNext()) {
            token = lexer.next();
            log.trace(String.format("token=%s, state=%s", token, state));
            if (state < 0)
                throw new Exception(String.format("no next state for token %s", token));
            List<State> plist = stateList.get(state);
            r = -1;
            for (int i = 0; i < plist.size(); i++) {
                State s = plist.get(i);
                if (s.predicate.test(token)) {
                    r = i;
                    log.trace(String.format("match, state=%d, index=%d", state, r));
                    break;
                }
            }
            if (r == -1) {
                log.trace(String.format("no match: token=%s, state=%d", token, state));
                throw new Exception("Parser error");
            }

            if (plist.get(r).action != null)
                parserResult = plist.get(r).action.doAction(token.toString(), parserResult);

            state = plist.get(r).nextStateNumber;
            log.trace(String.format("next state = %d", state));
            if (terminalStates.contains(state)) {
                log.trace(String.format("terminal state %d", state));
                break;
            }
        }
        return parserResult;
    }
}


