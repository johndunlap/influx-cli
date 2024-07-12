package org.voidzero.influx.cli.exception;

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

import java.lang.reflect.Field;
import org.voidzero.influx.cli.annotation.Arg;

/**
 * Thrown when a value cannot be parsed.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class ParseException extends Exception {
    /**
     * The default exit status to return when a field cannot be parsed.
     */
    public static final int DEFAULT_ERROR_EXIT_STATUS = 1;

    /**
     * The field which could not be populated.
     */
    private Field field;

    /**
     * The value that could not be parsed.
     */
    private String value;

    /**
     * Constructs a new exception with the specified field, value, and message.
     *
     * @param field The field which could not be populated
     * @param value The value that could not be parsed
     * @param message The message to include in the exception
     */
    public ParseException(Field field, String value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the exit status which should be passed back to the shell
     * for this exception. Allowing this to be different on a field by
     * field basis can allow shell programs to determine which fields
     * were invalid.
     *
     * @return The exit status which should be passed back to the shell
     */
    public int getExitStatus() {
        if (field != null && field.isAnnotationPresent(Arg.class)) {
            Arg arg = field.getAnnotation(Arg.class);
            return arg.exitStatus();
        }

        return DEFAULT_ERROR_EXIT_STATUS;
    }

    /**
     * Constructs a new exception with the specified value and message.
     *
     * @param value The value that could not be parsed
     * @param message The message to include in the exception
     */
    public ParseException(String value, String message) {
        super(message);
        this.value = value;
    }

    /**
     * Constructs a new exception with the specified field and message.
     *
     * @param field The field which could not be populated
     * @param message The message to include in the exception
     */
    public ParseException(Field field, String message) {
        super(message);
        this.field = field;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param message The message to include in the exception
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param message The message to include in the exception
     * @param cause The cause of the exception
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The cause of the exception
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns the field which could not be populated.
     *
     * @return The field which could not be populated
     */
    public Field getField() {
        return field;
    }

    /**
     * Returns the value that could not be parsed.
     *
     * @return The value that could not be parsed
     */
    public String getValue() {
        return value;
    }
}
