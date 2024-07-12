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

import java.util.List;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Command;
import org.voidzero.influx.cli.annotation.Ordered;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;

/**
 * Tests which verify the behavior of the {@link Ordered} annotation.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class OrderedTest {
    @Test
    public void testOrderedConfigWithFourElements() throws ParseException, HelpException {
        String[] args = new String[]{"zero", "one", "two", "three"};
        OrderedConfig orderedConfig = (OrderedConfig) new InfluxCli().bind(OrderedConfig.class, args);
        assertEquals("zero", orderedConfig.getStringValue());
        assertEquals("one", orderedConfig.getSomething());
        assertEquals("two", orderedConfig.getList().get(0));
        assertEquals("three", orderedConfig.getArray()[0]);
        assertEquals(1, orderedConfig.getArray().length);
    }

    @Test(expected = ParseException.class)
    public void testRequiredOrderedMissingMissing() throws ParseException, HelpException {
        String[] args = new String[]{};
        new InfluxCli().bind(OrderedConfig.class, args);
    }

    @Command(
            openingText = "This is the opening description",
            closingText = "This is the closing description"
    )
    private static class OrderedConfig {
        @Ordered(
                order = 1,
                required = true,
                collectionType =
                        String.class
        )
        private String something;
        @Ordered(
                order = 2,
                collectionType = String.class
        )
        private List<String> list;
        @Ordered(
                order = 0,
                collectionType = String.class
        )
        private String stringValue;
        @Ordered(
                order = 3,
                collectionType = String.class
        )
        private String[] array;

        public OrderedConfig() {
        }

        public String getSomething() {
            return something;
        }

        public OrderedConfig setSomething(String something) {
            this.something = something;
            return this;
        }

        public List<String> getList() {
            return list;
        }

        public OrderedConfig setList(List<String> list) {
            this.list = list;
            return this;
        }

        public String getStringValue() {
            return stringValue;
        }

        public OrderedConfig setStringValue(String stringValue) {
            this.stringValue = stringValue;
            return this;
        }

        public String[] getArray() {
            return array;
        }

        public OrderedConfig setArray(String[] array) {
            this.array = array;
            return this;
        }

        @Override
        public String toString() {
            return "OrderedConfig{"
                    + "longValue='" + something + '\''
                    + ", list=" + list
                    + ", stringValue='" + stringValue + '\''
                    + '}';
        }
    }
}
