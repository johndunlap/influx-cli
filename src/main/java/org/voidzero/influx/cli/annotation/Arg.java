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
 * This annotation is used to mark a field as a named option(code/flag).
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Arg {
    /**
     * The single character code for the option. This is expected to be a single character. For example, 'o'.
     *
     * @return The single character code for the option.
     */
    char code() default ' ';

    /**
     * The name of the option. This is expected to be two or more characters and hyphen cased. For example, "my-option".
     *
     * @return The name of the option.
     */
    String flag() default "";

    /**
     * The index into the array of ordered arguments(not specified with a code/flag). This index is used to determine
     * which ordered values are applied to which fields. This has no affect if code or flag are specified.
     *
     * @return The index into the array of ordered arguments.
     */
    int order() default -1;

    /**
     * This is ignored for fields which are not collections. For collections, this is the minimum number of values that
     * should be bound. If the number of values is less than this, an exception will be thrown. The default is 0.
     *
     * @return The minimum number of values that should be bound.
     */
    int min() default 0;

    /**
     * This is ignored for fields which are not collections. For collections, this is the maximum number of values that
     * should be bound. If the number of values is greater than this, an exception will be thrown. The default is 1.
     *
     * @return The maximum number of values that should be bound.
     */
    int max() default 1;

    /**
     * If set to a non-zero value, this is the exit status which will be set if binding fails for this property. The
     * default is 0. Setting this attribute to 0(the default) will have no effect as other properties may fail to
     * bind even if this one succeeds.
     *
     * @return The exit status which will be set if binding fails for this property.
     */
    int exitStatus() default 0;

    /**
     * True if the option requires a value. If true and if a value is not available, an exception will be thrown. If
     * used on a collection, this is the same as specifying a min of 1.
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
     * This is only necessary if the field is a type that is not supported by default. In that case, this type is
     * instantiated and used to parse the value.
     *
     * @return The class of the ValueParser to use.
     */
    Class<? extends TypeConverter<?>> converter() default StringValueParser.class;

    /**
     * This is the variable name which should be resolved to a value if one isn't provided by the user. When specified,
     * the resolution order is <b>System.getenv(String)</b> first and then <b>System.getProperty(String)</b> second.
     *
     * @return the variable name which should be resolved to a value if one isn't provided by the user
     */
    String environmentVariable() default "";
}
