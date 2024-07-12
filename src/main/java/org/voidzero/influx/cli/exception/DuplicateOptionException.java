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

/**
 * Thrown when a duplicate option is found.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class DuplicateOptionException extends ParseException {
    /**
     * The field which could not be populated.
     */
    private final Field field;

    /**
     * Constructs with the specified message and field.
     *
     * @param message The message to include in the exception
     * @param field The field which could not be populated
     */
    public DuplicateOptionException(String message, Field field) {
        super(message);
        this.field = field;
    }

    /**
     * Constructs with the specified message, cause, and field.
     *
     * @param message The message to include in the exception
     * @param cause The cause of the exception
     * @param field The field which could not be populated
     */
    public DuplicateOptionException(String message, Throwable cause, Field field) {
        super(message, cause);
        this.field = field;
    }

    /**
     * Constructs with the specified cause and field.
     *
     * @param cause The cause of the exception
     * @param field The field which could not be populated
     */
    public DuplicateOptionException(Throwable cause, Field field) {
        super(cause);
        this.field = field;
    }

    /**
     * Returns the field which could not be populated.
     *
     * @return The field which could not be populated
     */
    public Field getField() {
        return field;
    }
}
