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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;
import org.voidzero.influx.cli.exception.RethrownException;

/**
 * Tests for binding against string representations of dates.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class DateParserTest {
    @Test
    public void testAnnotatedDateParser() throws ParseException, HelpException {
        String[] args = {"--date-value", "2022-12-11"};
        DateConfigWithAnnotatedValueParser config = (DateConfigWithAnnotatedValueParser) new InfluxCli()
                .bind(DateConfigWithAnnotatedValueParser.class, args);
        assertNotNull(config);

        String result = new SimpleDateFormat(DateValueParser.DATE_FORMAT)
                .format(config.getDateValue());

        assertEquals("2022-12-11", result);
    }

    @Test
    public void testRegisteredDateParser() throws ParseException, HelpException {
        String[] args = {"--date-value", "2022-12-11"};
        DateConfigWithAnnotatedValueParser config = (DateConfigWithAnnotatedValueParser) new InfluxCli()
                .register(Date.class, new DateValueParser())
                .bind(DateConfigWithAnnotatedValueParser.class, args);

        assertNotNull(config);

        String result = new SimpleDateFormat(DateValueParser.DATE_FORMAT)
                .format(config.getDateValue());

        assertEquals("2022-12-11", result);
    }

    @Test
    public void testRegisteredDateParserCollection() throws ParseException, HelpException {
        String[] args = {"--date-value", "2022-12-11"};

        Map<Class<?>, TypeConverter<?>> typeConverters = new HashMap<Class<?>, TypeConverter<?>>(1) {{
                put(Date.class, new DateValueParser());
            }};

        DateConfigWithAnnotatedValueParser config = (DateConfigWithAnnotatedValueParser) new InfluxCli()
            .register(typeConverters)
            .bind(DateConfigWithAnnotatedValueParser.class, args);

        assertNotNull(config);

        String result = new SimpleDateFormat(DateValueParser.DATE_FORMAT)
                .format(config.getDateValue());

        assertEquals("2022-12-11", result);
    }

    private static class DateConfigWithAnnotatedValueParser {
        @Arg(flag = "date-value", code = 'D', converter = DateValueParser.class)
        private Date dateValue;

        public DateConfigWithAnnotatedValueParser() {
        }

        public Date getDateValue() {
            return dateValue;
        }

        public DateConfigWithAnnotatedValueParser setDateValue(Date dateValue) {
            this.dateValue = dateValue;
            return this;
        }
    }

    private static class DateConfigWithoutAnnotatedValueParser {
        @Arg(flag = "date-value", code = 'D')
        private Date dateValue;

        public DateConfigWithoutAnnotatedValueParser() {
        }

        public Date getDateValue() {
            return dateValue;
        }

        public DateConfigWithoutAnnotatedValueParser setDateValue(Date dateValue) {
            this.dateValue = dateValue;
            return this;
        }
    }

    private static class DateValueParser implements TypeConverter<Date> {
        public static final String DATE_FORMAT = "yyyy-MM-dd";

        public DateValueParser() {
        }

        @Override
        public Class<Date> getType() {
            return Date.class;
        }

        @Override
        public Date read(String value) throws ParseException {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(value);
            } catch (java.text.ParseException e) {
                throw new RethrownException(e);
            }
        }

        @Override
        public String write(Date value) throws ParseException {
            return new SimpleDateFormat(DATE_FORMAT).format(value);
        }
    }
}
