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
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Predicate;

public abstract class Parser {
    final private Logger log = LoggerFactory.getLogger(Parser.class);

    protected Lexer lexer;
    protected Set<Integer> terminalStates = ImmutableSet.of();
    protected Token.TokenFinder tokenFinder;
    protected ListIterator<Token> tokenIterator;

    static class State {
        int stateNumber;
        Token token;
        Predicate<Token> predicate;
        int nextStateNumber;
        ParserAction action;

        /**
         * @deprecated Will be removed in 2.0.0
         */
        @Deprecated
        State(int stateNumber, Predicate<Token> predicate, int nextStateNumber, ParserAction action) {
            this.stateNumber = stateNumber;
            this.predicate = predicate;
            this.nextStateNumber = nextStateNumber;
            this.action = action;
        }

        State(int stateNumber, Token token, int nextStateNumber, ParserAction action) {
            this.stateNumber = stateNumber;
            this.token = token;
            this.predicate = token.getPredicate();
            this.nextStateNumber = nextStateNumber;
            this.action = action;
        }
    }

    private final List<List<State>> stateList;

    /**
     * @deprecated Will be removed in 2.0.0
     */
    @Deprecated
    protected void addState(int state, Predicate<Token> predicate, int nextState, ParserAction action) {
        if (stateList.size() < state + 1) {
            if (stateList.size() < state + 1)
                for (int i = 0; i < state + 1; i++)
                    stateList.add(null);
            List<State> list = new ArrayList<>();
            stateList.set(state, list);
        }
        List<State> list = stateList.get(state);
        if (list == null) {
            list = new ArrayList<>();
            stateList.set(state, list);
        }
        list.add(new State(state, predicate, nextState, action));
    }

    protected void addState(int state, Token token, int nextState, ParserAction action) {
            if (stateList.size() < state + 1) {
                if (stateList.size() < state + 1)
                    for (int i=0; i<state + 1; i++)
                        stateList.add(null);
                List<State> list = new ArrayList<>();
                stateList.set(state, list);
            }
            List<State> list = stateList.get(state);
            if (list == null) {
                list = new ArrayList<>();
                stateList.set(state, list);
            }
        list.add(new State(state, token, nextState, action));
    }

    abstract protected void init();

    protected Parser() {
        this(Lexer.defaultSpecialChars);
    }

    protected Parser(Set<Character> lexicalSpecialChars) {
        this.stateList = new ArrayList<>();
        init();
        Class<?>[] declaredClasses = this.getClass().getDeclaredClasses();
        for (Class<?> aClass : declaredClasses) {
            if (Token.class.isAssignableFrom(aClass)) {
                //noinspection unchecked
                this.tokenFinder = Token.createTokenFinderFromClass((Class<Token>) aClass);
                break;
            }
        }
        this.lexer = new Lexer(lexicalSpecialChars, this.tokenFinder != null ? this.tokenFinder : Token.defaultTokenFinder);
    }

    public void printStateDiagram(@SuppressWarnings("SameParameterValue") PrintStream out) {
        for (List<State> l : stateList) {
            if (l == null)
                continue;
            for (State s : l) {
                String ts;
                if (s.token != null)
                    ts = s.token.toString();
                else
                    ts = s.predicate.toString();
                out.printf("%d %s %d%n", s.stateNumber, ts, s.nextStateNumber);
            }
        }
    }

    protected ParserResult resultInitializer() {
        return null;
    }
    
    public ParserResult parse(String source) throws ParserException {
        int state = 0;
        int r;
        ParserResult parserResult = resultInitializer();
        Token token;
        tokenIterator= lexer.tokenize(source);
        while (tokenIterator.hasNext()) {
            token = tokenIterator.next();
            log.trace(String.format("token=%s, state=%s", token, state));
            if (state < 0)
                throw new NoStateForTokenException(token.getValue());
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
                throw new NoMatchForTokenException(token.getValue(), state);
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


