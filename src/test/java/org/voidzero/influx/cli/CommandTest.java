package org.voidzero.influx.cli;

/*-
 * #%L
 * influx-cli
 * %%
 * Copyright (C) 2023 - 2024 John Dunlap
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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.annotation.Command;

/**
 * These tests verify that {@link org.voidzero.influx.cli.annotation.Command} behaves as expected.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class CommandTest {

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testHelpMessage() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommand.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "  -f  --first-name  Accepts a string value\n"
                + "  -l  --last-name   Accepts a string value\n"
                + "      --sub         Accepts a number\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommand {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private SubCommand sub;

        private String test;

        public RootCommand() {
        }

        public String getTest() {
            return test;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public SubCommand getSub() {
            return sub;
        }
    }

    /**
     * Example class for a sub command.
     */
    @Command
    private static class SubCommand {
        /**
         * A url.
         */
        @Arg(code = 'u')
        private String url;

        /**
         * A sort direction.
         */
        @Arg(code = 'd')
        private String direction;

        /**
         * Default no-arg constructor.
         */
        public SubCommand() {
        }

        /**
         * Returns the url.
         *
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * Returns the sort direction.
         *
         * @return sort direction
         */
        public String getDirection() {
            return direction;
        }
    }
}
