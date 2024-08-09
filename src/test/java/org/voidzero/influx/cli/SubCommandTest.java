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
public class SubCommandTest {

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testUnnamedSubCommandWithoutDescription() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommandWithUnnamedSubCommand.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "commands:\n"
                + "\tsub\t\t(description unavailable)\n"
                + "\n"
                + "  -f, --first-name  Accepts a string value\n"
                + "  -l, --last-name   Accepts a string value\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testUnnamedSubCommandWithDescription() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommandWithUnnamedSubCommandWithDescription.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "commands:\n"
                + "\tsub\t\tthis is the description\n"
                + "\n"
                + "  -f, --first-name  Accepts a string value\n"
                + "  -l, --last-name   Accepts a string value\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testNamedSubCommandWithoutDescription() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommandWithNamedSubCommand.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "commands:\n"
                + "\tsomething\t\t(description unavailable)\n"
                + "\n"
                + "  -f, --first-name  Accepts a string value\n"
                + "  -l, --last-name   Accepts a string value\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testNamedSubCommandWithDescription() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommandWithNamedSubCommandAndDescription.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "commands:\n"
                + "\tsomething\t\tcommand description\n"
                + "\n"
                + "  -f, --first-name  Accepts a string value\n"
                + "  -l, --last-name   Accepts a string value\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Verify that the help message for the sub command is correctly formatted.
     */
    @Test
    public void testMultipleNamedSubCommandsWithDescription() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        new InfluxCli()
                .setOut(printStream)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(RootCommandWithMultipleNamedSubCommandAndDescription.class, new String[]{"--help"});

        String expected = "The following options are accepted: \n"
                + "commands:\n"
                + "\tsomething\t\tcommand description\n"
                + "\tsomething\t\tcommand description\n"
                + "\n"
                + "  -f, --first-name  Accepts a string value\n"
                + "  -l, --last-name   Accepts a string value\n"
                + "      --test        Accepts a string value\n";

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommandWithUnnamedSubCommand {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private UnnamedSubCommand sub;

        private String test;

        public RootCommandWithUnnamedSubCommand() {
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

        public UnnamedSubCommand getSub() {
            return sub;
        }
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommandWithUnnamedSubCommandWithDescription {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private UnnamedSubCommandWithDescription sub;

        private String test;

        public RootCommandWithUnnamedSubCommandWithDescription() {
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

        public UnnamedSubCommandWithDescription getSub() {
            return sub;
        }
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommandWithNamedSubCommand {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private NamedSubCommand sub;

        private String test;

        public RootCommandWithNamedSubCommand() {
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

        public NamedSubCommand getSub() {
            return sub;
        }
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommandWithMultipleNamedSubCommandAndDescription {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private NamedSubCommandWithDescription sub1;

        private NamedSubCommandWithDescription sub2;

        private String test;

        public RootCommandWithMultipleNamedSubCommandAndDescription() {
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

        public NamedSubCommandWithDescription getSub1() {
            return sub1;
        }

        public NamedSubCommandWithDescription getSub2() {
            return sub2;
        }
    }

    /**
     * Sample config object used only for tests.
     */
    private static class RootCommandWithNamedSubCommandAndDescription {
        @Arg(code = 'f')
        private String firstName;

        @Arg(code = 'l')
        private String lastName;

        private NamedSubCommandWithDescription sub;

        private String test;

        public RootCommandWithNamedSubCommandAndDescription() {
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

        public NamedSubCommandWithDescription getSub() {
            return sub;
        }
    }

    /**
     * Example class for a sub command.
     */
    @Command
    private static class UnnamedSubCommand {
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
        public UnnamedSubCommand() {
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

    /**
     * Example class for a sub command.
     */
    @Command(description = "this is the description")
    private static class UnnamedSubCommandWithDescription {
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
        public UnnamedSubCommandWithDescription() {
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

    /**
     * Example class for a sub command.
     */
    @Command(name = "something")
    private static class NamedSubCommand {
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
        public NamedSubCommand() {
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

    /**
     * Example class for a sub command.
     */
    @Command(
            name = "something",
            description = "command description"
    )
    private static class NamedSubCommandWithDescription {
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
        public NamedSubCommandWithDescription() {
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
