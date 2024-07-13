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

/**
 * This annotation identifies classes which represent the arguments of a CLI command. For example, in the AWS CLI,
 * <pre>aws s3 cp ...</pre> the root-command would be "aws", "s3" would be a sub-command within "aws", and "cp"
 * would be a sub-command within "s3". This concept allows the options for one command to be kept separate from
 * options for other commands.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    /**
     * The name of the context. If a name is not provided, the name of the annotated field will be used.
     *
     * @return The name of the context or empty string if one is not provided.
     */
    String name() default "";

    /**
     * A description of the context which can be displayed in help messages.
     *
     * @return A description of the context which can be displayed in help messages.
     */
    String description() default "(description unavailable)";
    /**
     * The text to display prior to the list of options.
     *
     * @return The text to display prior to the list of options.
     */
    String openingText() default "";

    /**
     * The text to display after the list of options.
     *
     * @return The text to display after the list of options.
     */
    String closingText() default "";

    /**
     * The tokens which should trigger the display of the help message. Defaults to "-h" and "--help".
     *
     * @return The tokens which should trigger the display of the help message.
     */
    String[] helpTokens() default {"-h", "--help"};
}
