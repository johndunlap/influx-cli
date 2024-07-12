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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Arg;

/**
 * Tests for configuration classes which implement {@link Runnable}.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class EntryPointTest {
    @Test
    public void testRunOnSimpleObjectWithValidArguments() {
        String[] args = new String[]{"--first", "abc123", "--second", "80"};
        SimpleConfig config = (SimpleConfig) new InfluxCli()
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(SimpleConfig.class, args);
        assertNotNull(config);
        assertEquals("abc123", config.getFirst());
        assertEquals(80, config.getSecond());
    }

    @Test
    public void testRunOnSimpleObjectWithInvalidArguments() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(outputStream);
        String[] args = new String[]{"--first", "80", "--second", "abc123"};

        try {
            new InfluxCli()
                    .setErr(err)
                    .setExitMechanism(status -> {
                        if (status != 0) {
                            throw new RuntimeException("Exit called with status " + status);
                        }
                    })
                    .bindOrExit(SimpleConfig.class, args);
        } catch (RuntimeException e) {
            assertEquals(
                    "Failed to parse string abc123 into an instance of class int\n",
                    outputStream.toString()
            );
        }
    }

    @Test
    public void testRunOnSimpleObjectWithHelpFlag() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);
        String[] args = new String[]{"--help"};

        new InfluxCli()
                .setOut(out)
                .setExitMechanism(status -> {
                    if (status != 0) {
                        throw new RuntimeException("Exit called with status " + status);
                    }
                })
                .bindOrExit(SimpleConfig.class, args);

        String expected = "The following options are accepted: \n"
                + "  -f  --first    Accepts a string value (required)\n"
                + "      --second   Accepts a number\n"
                + "      --invoked  Boolean flag which requires no argument\n";

        assertEquals(
                expected,
                outputStream.toString()
        );
    }

    private static class SimpleConfig implements Runnable {
        @Arg(required = true, code = 'f')
        private String first;
        private int second;

        private Boolean invoked;

        public SimpleConfig() {
        }

        public String getFirst() {
            return first;
        }

        public SimpleConfig setFirst(String first) {
            this.first = first;
            return this;
        }

        public int getSecond() {
            return second;
        }

        public SimpleConfig setSecond(int second) {
            this.second = second;
            return this;
        }

        public Boolean getInvoked() {
            return invoked;
        }

        public SimpleConfig setInvoked(Boolean invoked) {
            this.invoked = invoked;
            return this;
        }

        @Override
        public void run() {
            assertEquals("abc123", getFirst());
            assertEquals(80, getSecond());

            invoked = true;
        }
    }
}
