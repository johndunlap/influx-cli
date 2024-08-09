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

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.voidzero.influx.cli.annotation.Command;
import org.voidzero.influx.cli.annotation.Ignore;
import org.voidzero.influx.cli.annotation.Ordered;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;

/**
 * The main entry point for the influx-cli library.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class InfluxCli {

    /**
     * Descriptions for named options wrap around to the next line after this threshold.
     */
    public static final int WORDWRAP_THRESHOLD = 80;

    /**
     * This allows unit tests to override the exit mechanism.
     */
    private ExitMechanism exitMechanism = System::exit;

    /**
     * This allows unit tests to override the output stream.
     */
    private PrintStream out = System.out;

    /**
     * This allows unit tests to override the error stream.
     */
    private PrintStream err = System.err;

    private final Map<Class<?>, TypeConverter<?>> typeConverters = new HashMap<>();

    /**
     * Default constructor.
     */
    public InfluxCli() {
    }

    /**
     * Registers a type converter for the given type.
     *
     * @param type The type to register the type converter for
     * @param typeConverter The type converter to register
     *
     * @return Self reference to support method chaining
     */
    public InfluxCli register(Class<?> type, TypeConverter<?> typeConverter) {
        typeConverters.put(type, typeConverter);
        return this;
    }

    /**
     * Registers a map of type converters.
     *
     * @param typeConverters The type converters to register
     *
     * @return Self reference to support method chaining
     */
    public InfluxCli register(Map<Class<?>, TypeConverter<?>> typeConverters) {
        this.typeConverters.putAll(typeConverters);
        return this;
    }

    /**
     * Same as {@link #bind(Class, String[])} except that it returns a {@link ParseContext} instead
     * of the instance.
     *
     * @param classType The class type to bind the arguments to
     * @param args The arguments to bind to the class type
     *
     * @return A {@link ParseContext} containing the instance of the class type with the arguments
     * @throws ParseException If the arguments could not be bound to the class type
     * @throws HelpException thrown if a flag in the args parameter is requesting help
     */
    public ParseContext<?> bindContext(Class<?> classType, String[] args) throws ParseException, HelpException {
        // Is the first argument specifying a context for a sub-command?
        if (args.length > 0 && args[0].charAt(0) != '-') {
            for (Field field : classType.getDeclaredFields()) {
                Command command = field.getType().getDeclaredAnnotation(Command.class);

                if (command != null) {
                    String contextName = command.name().isEmpty() ? field.getName() : command.name();

                    // If the first argument exactly matches a context on the classType, recurse into it
                    if (args[0].equals(contextName)) {
                        // Remove the context name from the front of the args array
                        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

                        // Recurse into the context
                        return bindContext(field.getType(), newArgs);
                    }
                }
            }
        }

        ParseContext<?> context = new ParseContext<>(classType, args, typeConverters);

        Parser state = Parser.NEUTRAL;

        // Continue executing the next state until all input has been processed
        while (state != null) {
            state = state.execute(context);
        }

        Object instance = context.getInstance();

        // Verify that required fields are set
        for (Field field : context.getRequiredFields()) {
            try {
                Object value = ReflectionUtil.getFieldValue(field, instance);

                if (value == null) {
                    // TODO: This does not take annotations into account. The java field name will be used
                    //  even if it has been overridden by an annotation.
                    throw new ParseException(field, "Required argument --"
                            + Parser.camelCaseToHyphenCase(field.getName())
                            + " is not set");
                }
            } catch (IllegalAccessException e) {
                throw new ParseException("Could not access field " + field.getName(), e);
            }
        }

        return context;
    }

    /**
     * This method is used to display the help message for the given class type. The help message
     *
     * @param classType The class type to display the help message for
     * @param <T> The type of the class to display the help message for
     */
    protected <T> void showHelp(Class<T> classType) {
        // Print the help message to stdout
        out.println(help(classType));

        // Exit normally
        exitMechanism.exit(0);
    }

    /**
     * Creates an instance of the specified type and populates it with values taken from the string of arguments.
     *
     * @param classType the class type of the instance which should be created
     * @param args the arguments which should be used to populate the instance
     * @return the populated(bound) instance
     * @throws ParseException Thrown if the arguments cannot be bound to the instance
     * @throws HelpException Thrown if one of the arguments is requesting help
     */
    public Object bind(Class<?> classType, String[] args) throws ParseException, HelpException {
        // Bind the arguments to the class type
        ParseContext<?> context = bindContext(classType, args);

        // Get the instance from the parse context
        return context.getInstance();
    }

    /**
     * This method automates the mechanics behind binding, error handling, and displaying
     * the help message. Incoming arguments are bound to the specified class type and the run method
     * is invoked after binding has been completed. If binding fails, validation messages will be
     * printed to stderr and an appropriate exit status will be set. If the help message is
     * requested, it will be printed to stdout prior to exit.
     *
     * @param classType The class type to bind the arguments to
     * @param args The arguments to bind to the class type
     *
     * @return An instance of the specified class type with the specified arguments bound to it. An exception will be
     *     thrown if binding fails
     */
    public Object bindOrExit(Class<?> classType, String[] args) {
        Object instance = null;

        try {
            // Bind the arguments to the class type
            ParseContext<?> context = bindContext(classType, args);

            // Get the instance from the parse context
            instance = context.getInstance();

            // We don't need to worry about setting the exit status to 0 because that is
            // the default behavior for the JVM
            return instance;
        } catch (HelpException e) {
            showHelp(e.getClassType());
            return null;
        } catch (ParseException e) {
            // Print the error message to stderr
            getErr().println(e.getMessage());

            exitMechanism.exit(e.getExitStatus());

            // This should only happen when the exit mechanism is overridden. Realistically, it probably won't execute
            // even then because the tests will use the exit mechanism to throw an exception.
            return instance;
        }
    }

    /**
     * Generates a help message for the given class type.
     *
     * @param classType The class type to generate a help message for
     * @param <T> The type of the class for which a help message should be generated
     * @return A help message for the given class type
     */
    public static <T> String help(Class<T> classType) {
        Command command = classType.getAnnotation(Command.class);
        StringBuilder sb = new StringBuilder();
        String before = "";
        String after = "";

        // If there's a help annotation, grab the before and after text
        if (command != null) {
            before = command.openingText();
            after = command.closingText();
        } else {
            before = "The following options are accepted: ";
        }

        sb.append(before);

        List<OptionInfo> commandList = new ArrayList<>();
        Map<String, List<OptionInfo>> categorized = new HashMap<>();
        int longestLongName = 0;

        // Attempt to categorize the options
        Map<String, Boolean> categoryMap = new HashMap<>();
        for (OptionInfo optionInfo : extract(classType)) {
            if (optionInfo.isCommand()) {
                commandList.add(optionInfo);
                continue;
            }

            String category = optionInfo.getCategory();

            if (category.isEmpty()) {
                category = "default";
            } else {
                categoryMap.put(category, true);
            }

            List<OptionInfo> list;

            // Create the list if it doesn't already exist
            if (!categorized.containsKey(category)) {
                list = new ArrayList<>();
                categorized.put(category, list);
            } else {
                list = categorized.get(category);
            }

            list.add(optionInfo);

            // Keep track of the longest long name so that we can pad the help message correctly
            int length = optionInfo.getFlag().length();
            if (length > longestLongName) {
                longestLongName = length;
            }
        }

        if (!commandList.isEmpty()) {
            sb.append("\ncommands:");

            // Emit the list of available sub-commands
            for (OptionInfo optionInfo : commandList) {
                sb.append("\n\t")
                        .append(optionInfo.getCommandName());


                if (!optionInfo.getDescription().isEmpty()) {
                    sb.append("\t\t")
                            .append(optionInfo.getDescription());
                }
            }

            sb.append("\n");
        }

        LinkedList<String> categoryList = new LinkedList<>(categoryMap.keySet());

        // Sort the list of categories
        Collections.sort(categoryList);

        categoryList.addFirst("default");

        String longestWhitespace = String.join("", Collections.nCopies(longestLongName, " "));
        String leftPadding = " ".repeat(10 + longestLongName);

        for (String category : categoryList) {
            List<OptionInfo> namedOptionList = categorized.get(category);

            if (!category.equals("default")) {
                sb.append("\n\n").append(category).append(":");
            }

            for (OptionInfo nameOption : namedOptionList) {
                sb.append("\n");
                StringBuilder lineBuilder = new StringBuilder();

                if (nameOption.isRequired()) {
                    lineBuilder.append("* ");
                } else {
                    lineBuilder.append("  ");
                }

                if (nameOption.getCode() != ' ') {
                    lineBuilder.append('-').append(nameOption.getCode());
                } else {
                    lineBuilder.append("  ");
                }

                String longName = nameOption.getFlag();

                // Pad the end of the long name
                if (longName.isEmpty()) {
                    lineBuilder.append(longestWhitespace);
                } else {
                    if (nameOption.getCode() != ' ') {
                        lineBuilder.append(',');
                    } else {
                        lineBuilder.append(' ');
                    }

                    String currentLine = lineBuilder.append(" --").append(longName)
                            .append(longestWhitespace, 0, longestLongName - longName.length())
                            .append("  ")
                            .append(nameOption.getDescription())
                            .toString();

                    if (currentLine.length() > WORDWRAP_THRESHOLD) {
                        int lastSpaceIndex = currentLine.lastIndexOf(' ', WORDWRAP_THRESHOLD - 1);
                        sb.append(currentLine, 0, lastSpaceIndex)
                                .append('\n');
                        String remainder = currentLine.substring(lastSpaceIndex + 1);
                        sb.append(Parser.wordWrap(remainder, WORDWRAP_THRESHOLD, leftPadding))
                                .append('\n');
                    } else {
                        sb.append(currentLine);
                    }
                }
            }
        }

        if (!after.isEmpty()) {
            sb.append("\n");
        }

        return sb.append(after).toString();
    }

    /**
     * This method is shared between the bind and help methods.
     *
     * @param classType The class type from which metadata should be extracted
     * @param <T> The generic type of the class from which metadata is being extracted
     * @return A list of objects representing the fields which can be bound to
     */
    protected static <T> List<OptionInfo> extract(Class<T> classType) {
        List<OptionInfo> options = new ArrayList<>();
        Field[] fields = classType.getDeclaredFields();

        for (Field field : fields) {
            // Skip ordered fields and fields which have been annotated with ignore
            if (field.isAnnotationPresent(Ignore.class) || field.isAnnotationPresent(Ordered.class)) {
                continue;
            }

            options.add(new OptionInfo(field));
        }

        return options;
    }

    /**
     * This method is used to override which print stream is used for output.
     *
     * @param out The print stream to use for output
     *
     * @return Self reference to support method chaining
     */
    public InfluxCli setOut(PrintStream out) {
        this.out = out;
        return this;
    }

    /**
     * This method is used to override which print stream is used for error messages.
     *
     * @param err The print stream to use for error messages
     *
     * @return Self reference to support method chaining
     */
    public InfluxCli setErr(PrintStream err) {
        this.err = err;
        return this;
    }

    /**
     * This method is used to override the exit mechanism.
     *
     * @param exitMechanism The exit mechanism to use
     *
     * @return Self reference to support method chaining
     */
    public InfluxCli setExitMechanism(ExitMechanism exitMechanism) {
        this.exitMechanism = exitMechanism;
        return this;
    }

    /**
     * This method is used to retrieve the current print stream used for output.
     *
     * @return The current print stream used for output
     */
    public PrintStream getOut() {
        return out;
    }

    /**
     * This method is used to retrieve the current print stream used for error messages.
     *
     * @return The current print stream used for error messages
     */
    public PrintStream getErr() {
        return err;
    }
}
