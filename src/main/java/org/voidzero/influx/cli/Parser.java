package org.voidzero.influx.cli;

/*-
 * #%L
 * influx-cli
 * %%
 * Copyright (C) 2024 John Dunlap
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;
import org.voidzero.influx.cli.exception.RethrownException;

/**
 * The parser is responsible for parsing the command line arguments.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public enum Parser {

    /**
     * In this state, the state machine will look ahead in the argument list to determine which state
     * it needs to transition into.
     */
    NEUTRAL {
        @Override
        protected <T> Parser execute(ParseContext<T> context) throws ParseException {
            try {
                // There is nothing left to do
                if (context.getQueue().isEmpty()) {
                    return null;
                }

                String arg = context.getQueue().peek();

                if (arg.charAt(0) == '-') {
                    if (arg.charAt(1) != '-') {
                        // This is a single character flag and doesn't need to be expanded
                        if (arg.length() == 2) {
                            return FLAG;
                        }

                        // Remove the argument from the stack because it needs to be expanded into multiple arguments
                        arg = context.getQueue().pop();

                        // Convert the argument string(without the hyphen) into an array of characters
                        char[] characters = arg.substring(1).toCharArray();

                        // Expand multiple single letter options into multiple single letter options by looping
                        // through the array in reverse order and adding them to the stack as individual arguments
                        // so that the first character in the string is processed first
                        for (int i = characters.length - 1; i >= 0; i--) {
                            context.getQueue().push("-" + characters[i]);
                        }

                    }
                    return FLAG;
                }

                // Set the next ordered value
                context.setOrderedValue(arg);
                context.getQueue().pop();
                return NEUTRAL;
            } catch (RuntimeException e) {
                throw new RethrownException(e);
            }
        }
    },
    /**
     * In this state, the state machine will attempt to extract a flag from the argument list.
     */
    FLAG {
        @Override
        protected <T> Parser execute(ParseContext<T> context) throws ParseException, HelpException {
            try {
                String arg = context.getQueue().pop();

                // Stop parsing because help was requested
                if (context.isHelpToken(arg)) {
                    throw new HelpException(context.getInstance().getClass());
                }

                if (arg.charAt(0) == '-') {
                    if (arg.charAt(1) == '-') {
                        context.setCurrentName(arg.substring(2));
                    } else {
                        context.setCurrentName(arg.substring(1));
                    }

                    return VALUE;
                } else {
                    // Set the next ordered value
                    context.setOrderedValue(arg);
                    context.getQueue().pop();
                    return NEUTRAL;
                }
            } catch (RuntimeException e) {
                throw new RethrownException(e);
            }
        }
    },

    /**
     * In this state, the state machine will attempt to extract a value from the argument list.
     */
    VALUE {
        @Override
        protected <T> Parser execute(ParseContext<T> context) throws ParseException {
            try {
                // Directly handle booleans which don't need values
                if (context.isBoolean()) {
                    context.setNamedValue("true");
                    return NEUTRAL;
                }

                // Create an error when we don't have enough arguments
                if (context.getQueue().isEmpty()) {
                    // TODO: Should we throw an exception if we have no arguments and required fields?
                    return null;
                }

                // Set the value
                context.setNamedValue(context.getQueue().pop());

                return NEUTRAL;
            } catch (RuntimeException e) {
                throw new RethrownException(e);
            }
        }
    };

    /**
     * Execute the parser with the given context.
     *
     * @param context The context
     * @param <T> The type of the context
     *
     * @return The next parser
     *
     * @throws ParseException If an error occurs
     * @throws HelpException If help is requested
     */
    protected abstract <T> Parser execute(ParseContext<T> context) throws ParseException, HelpException;

    /**
     * Convert a camel case string to hyphen case.
     *
     * @param camelCase A camel case string
     * @return hyphen case string
     */
    public static String camelCaseToHyphenCase(String camelCase) {
        StringBuilder hyphenCase = new StringBuilder();

        char firstCharacter = camelCase.charAt(0);
        hyphenCase.append(toLowerCase(firstCharacter));

        for (int x = 1; x < camelCase.length(); x++) {
            char currentCharacter = camelCase.charAt(x);

            if (isUpperCase(currentCharacter)) {
                hyphenCase.append('-').append(toLowerCase(currentCharacter));
            } else {
                hyphenCase.append(currentCharacter);
            }
        }

        return hyphenCase.toString();
    }

    /**
     * Convert a hyphen case string to camel case.
     *
     * @param hyphenCase A hyphen case string
     * @return camel case string
     */
    public static String hyphenCaseToCamelCase(String hyphenCase) {
        String[] parts = hyphenCase.split("-");
        StringBuilder sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            char[] chars = part.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            part = new String(chars);
            sb.append(part);
        }

        return sb.toString();
    }
}
