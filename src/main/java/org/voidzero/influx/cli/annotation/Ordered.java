package org.voidzero.influx.cli.annotation;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.voidzero.influx.cli.StringValueParser;
import org.voidzero.influx.cli.TypeConverter;

/**
 * Annotation used to mark a field as an ordered argument. Ordered arguments are arguments which are not specified with
 * a code/flag. They are simply specified in the order in which they appear in the command line.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ordered {
    /**
     * The index into the array of ordered arguments(not specified with a code/flag). This index is used to determine
     * which orderd values are applied to which fields.
     *
     * @return The index into the array of ordered arguments.
     */
    int order();

    /**
     * True if the option requires a value. If true and if a value is not available, an exception will be thrown.
     *
     * @return True if the option requires a value.
     */
    boolean required() default false;

    /**
     * The category in which the option should appear in the help message. This is used in the help message.
     *
     * @return The category of the option.
     */
    String category() default "";

    /**
     * The description of the option. This is used in the help message.
     *
     * @return The description of the option.
     */
    String description() default "";

    /**
     * This is only necessary if the field is a collection. In that case, this is the type of the objects which the
     * collection will contain. This is necessary because of Java type erasure. Generics are not available at runtime.
     *
     * @return The type of the objects which the collection will contain.
     */
    Class<?> collectionType() default Object.class;


    /**
     * The parser to use for this option. This is only necessary when the field type is not supported.
     *
     * @return The parser to use for this option.
     */
    Class<? extends TypeConverter<?>> converter() default StringValueParser.class;
}
