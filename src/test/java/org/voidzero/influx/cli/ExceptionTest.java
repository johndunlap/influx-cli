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

import java.lang.reflect.Field;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.exception.DuplicateOptionException;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.InaccessibleFieldException;
import org.voidzero.influx.cli.exception.MissingNoArgConstructorException;
import org.voidzero.influx.cli.exception.ParseException;
import org.voidzero.influx.cli.exception.RethrownException;
import org.voidzero.influx.cli.exception.UnsupportedTypeConversionException;

/**
 * Tests for exceptions.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class ExceptionTest {
    @Test(expected = InaccessibleFieldException.class)
    public void testInaccessibleFieldExceptionConstructorMessageThrowableClassType()
            throws InaccessibleFieldException {
        throw new InaccessibleFieldException("Message", new Exception(), Integer.class);
    }

    @Test(expected = MissingNoArgConstructorException.class)
    public void testMissingDefaultConstructorExceptionConstructorMessageThrowableClassType()
            throws MissingNoArgConstructorException {
        throw new MissingNoArgConstructorException("Message", new Exception(), Integer.class);
    }

    @Test(expected = ParseException.class)
    public void testNumericParseExceptionConstructorFieldValueMessage() throws ParseException {
        Field field = NamedConfig.class.getDeclaredFields()[0];
        String value = "123";
        String message = "Message";
        ParseException npe = new ParseException(field, value, message);
        assertEquals(field, npe.getField());
        assertEquals(value, npe.getValue());
        assertEquals(message, npe.getMessage());
        throw npe;
    }

    @Test(expected = RethrownException.class)
    public void testRethrownRuntimeExceptionConstructorThrowable()
            throws RethrownException {
        throw new RethrownException(new Exception());
    }

    @Test(expected = UnsupportedTypeConversionException.class)
    public void testUnsupportedTypeConversionExceptionConstructorMessage()
            throws UnsupportedTypeConversionException {
        throw new UnsupportedTypeConversionException("Message");
    }

    @Test(expected = ParseException.class)
    public void testParseExceptionConstructorMessage() throws ParseException {
        throw new ParseException("Message");
    }

    @Test(expected = ParseException.class)
    public void testParseExceptionConstructorMessageThrowable() throws ParseException {
        throw new ParseException("Message", new Exception());
    }

    @Test(expected = ParseException.class)
    public void testParseExceptionConstructorThrowable() throws ParseException {
        throw new ParseException(new Exception());
    }

    @Test(expected = DuplicateOptionException.class)
    public void testDuplicateLongName() throws ParseException, HelpException {
        String[] args = {"--value", "abc123"};
        new InfluxCli().bind(DuplicateLongNameConfig.class, args);
    }

    @Test(expected = DuplicateOptionException.class)
    public void testDuplicateShortName() throws ParseException, HelpException {
        String[] args = {"--value", "abc123"};
        new InfluxCli().bind(DuplicateShortNameConfig.class, args);
    }

    @Test
    public void testCustomExitStatus() throws ParseException, HelpException {
        try {
            new InfluxCli().bind(CustomExitStatus.class, new String[]{});
        } catch (ParseException e) {
            assertEquals(7, e.getExitStatus());
        }
    }

    private static class DuplicateLongNameConfig {
        @Arg(flag = "value")
        private String firstValue;
        @Arg(flag = "value")
        private String secondValue;

        public DuplicateLongNameConfig() {
        }

        public String getFirstValue() {
            return firstValue;
        }

        public DuplicateLongNameConfig setFirstValue(String firstValue) {
            this.firstValue = firstValue;
            return this;
        }

        public String getSecondValue() {
            return secondValue;
        }

        public DuplicateLongNameConfig setSecondValue(String secondValue) {
            this.secondValue = secondValue;
            return this;
        }
    }

    private static class DuplicateShortNameConfig {
        @Arg(code = 'V')
        private String firstValue;
        @Arg(code = 'V')
        private String secondValue;

        public DuplicateShortNameConfig() {
        }

        public String getFirstValue() {
            return firstValue;
        }

        public DuplicateShortNameConfig setFirstValue(String firstValue) {
            this.firstValue = firstValue;
            return this;
        }

        public String getSecondValue() {
            return secondValue;
        }

        public DuplicateShortNameConfig setSecondValue(String secondValue) {
            this.secondValue = secondValue;
            return this;
        }
    }

    private static class NamedConfig {
        private static String message;

        public NamedConfig() {
        }

        public static String getMessage() {
            return message;
        }

        public static void setMessage(String message) {
            NamedConfig.message = message;
        }
    }

    private static class CustomExitStatus {
        private String first;

        @Arg(exitStatus = 7)
        private String second;

        public CustomExitStatus() {
        }

        public String getFirst() {
            return first;
        }

        public CustomExitStatus setFirst(String first) {
            this.first = first;
            return this;
        }

        public String getSecond() {
            return second;
        }

        public CustomExitStatus setSecond(String second) {
            this.second = second;
            return this;
        }
    }
}
