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
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.voidzero.influx.cli.annotation.Ignore;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;

/**
 * These tests verify that fields annotated with {@link Ignore} are not
 * bound to command line arguments.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class IgnoreTest {

    @Test
    public void testIgnoredField() throws ParseException, HelpException {
        String[] args = new String[]{"--first", "abc123", "--second", "80"};
        IgnoreTestEntity config = (IgnoreTestEntity) new InfluxCli().bind(IgnoreTestEntity.class, args);
        assertNotNull(config);
        assertEquals("abc123", config.getFirst());
        assertNull(config.getSecond());
    }

    private static class IgnoreTestEntity {
        private String first;

        @Ignore
        private String second;

        public IgnoreTestEntity() {
        }

        public String getFirst() {
            return first;
        }

        public IgnoreTestEntity setFirst(String first) {
            this.first = first;
            return this;
        }

        public String getSecond() {
            return second;
        }

        public IgnoreTestEntity setSecond(String second) {
            this.second = second;
            return this;
        }
    }
}
