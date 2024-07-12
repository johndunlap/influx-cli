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

import org.voidzero.influx.cli.exception.ParseException;

/**
 * Implementations of this interface are used to parse string values into object instances which are not
 * supported by default.
 *
 * @param <P> The type of object that string values should be parsed into
 */
public interface TypeConverter<P> {
    /**
     * Returns the type of object that string values should be parsed into.
     *
     * @return The type of object that string values should be parsed into
     */
    Class<P> getType();

    /**
     * Parses a string value into an object instance.
     *
     * @param value The string value to parse
     *
     * @return The object instance
     * @throws ParseException If the string value cannot be parsed
     */
    P read(String value) throws ParseException;

    /**
     * Writes an object instance into a string value.
     *
     * @param value The object instance to write
     *
     * @return The string value
     * @throws ParseException If the object instance cannot be written
     */
    String write(P value) throws ParseException;
}
