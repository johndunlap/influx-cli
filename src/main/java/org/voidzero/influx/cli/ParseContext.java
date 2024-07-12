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

import static java.lang.String.format;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.annotation.Command;
import org.voidzero.influx.cli.annotation.Ignore;
import org.voidzero.influx.cli.annotation.Ordered;
import org.voidzero.influx.cli.exception.DuplicateOptionException;
import org.voidzero.influx.cli.exception.InaccessibleFieldException;
import org.voidzero.influx.cli.exception.MissingNoArgConstructorException;
import org.voidzero.influx.cli.exception.ParseException;
import org.voidzero.influx.cli.exception.RethrownException;
import org.voidzero.influx.cli.exception.UnsupportedTypeConversionException;

/**
 * Maintains the state of the parsing process.
 *
 * @param <T> The type of the object being populated with parsed arguments
 */
public class ParseContext<T> {
    private final Map<String, Field> namedFields = new HashMap<>();
    private final List<Field> orderedFields = new ArrayList<>();
    private final List<Field> requiredFields = new ArrayList<>();
    private final Stack<String> queue;
    private final T instance;
    private final Map<Class<?>, TypeConverter<?>> typeConverters;
    private final Set<String> helpTokens = new HashSet<>();
    private String currentName;
    private int currentOrderedIndex = 0;

    /**
     * Create a new ParseContext for the given class type and string arguments.
     *
     * @param classType The class type which will be instantiated and populated with the given arguments
     * @param args The string arguments to parse
     * @param typeConverters The map of value parsers to use when parsing values
     * @throws MissingNoArgConstructorException If the class type does not have a public default constructor
     */
    public ParseContext(Class<T> classType, String[] args, Map<Class<?>, TypeConverter<?>> typeConverters)
            throws ParseException {
        this.queue = new Stack<>();
        this.typeConverters = typeConverters;

        Command commandAnnotation;

        if (classType.getDeclaredAnnotation(Command.class) != null) {
            commandAnnotation = classType.getDeclaredAnnotation(Command.class);
        } else {
            commandAnnotation = AnnotationDefaults.class.getDeclaredAnnotation(Command.class);
        }

        helpTokens.addAll(Arrays.asList(commandAnnotation.helpTokens()));

        // Add the string args to the stack in reverse order
        for (int i = args.length - 1; i >= 0; i--) {
            this.queue.push(args[i]);
        }

        // Attempt to construct the instance which will be returned
        try {
            this.instance = classType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            String message = format("Class %s must have a public no-arg constructor", classType.getCanonicalName());
            throw new MissingNoArgConstructorException(message, e, classType);
        }

        // Associate flag names with class fields
        for (Field field : classType.getDeclaredFields()) {
            // Ignore fields marked with the @Ignore annotation
            if (field.getAnnotation(Ignore.class) != null) {
                continue;
            }

            Ordered orderedAnnotation = field.getAnnotation(Ordered.class);

            if (orderedAnnotation != null) {
                orderedFields.add(field);

                // Remember required fields
                if (orderedAnnotation.required()) {
                    requiredFields.add(field);
                }
            } else {
                Arg namedOption = field.getAnnotation(Arg.class);

                // Initialize boolean fields to false by default
                if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                    try {
                        ReflectionUtil.setFieldValue(field, instance, false);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (namedOption != null) {
                    // Remember required fields
                    if (namedOption.required()) {
                        requiredFields.add(field);
                    }

                    if (!namedOption.flag().equals("")) {
                        if (!namedFields.containsKey(namedOption.flag())) {
                            namedFields.put(namedOption.flag(), field);
                        } else {
                            throw new DuplicateOptionException("Duplicate option name: " + namedOption.flag(), field);
                        }
                    } else {
                        // Attempt to infer usable flag from the field name. No attempt is made to infer a code because
                        // conflicts are inevitable.
                        String longName = Parser.camelCaseToHyphenCase(field.getName());

                        if (!namedFields.containsKey(longName)) {
                            namedFields.put(longName, field);
                        }
                    }

                    if (namedOption.code() != ' ') {
                        if (!namedFields.containsKey(namedOption.code() + "")) {
                            namedFields.put(namedOption.code() + "", field);
                        } else {
                            throw new DuplicateOptionException("Duplicate option name: " + namedOption.code(), field);
                        }
                    }
                } else {
                    // Attempt to infer usable flag from the field name. No attempt is made to infer a code because
                    // conflicts are inevitable.
                    String longName = Parser.camelCaseToHyphenCase(field.getName());

                    if (!namedFields.containsKey(longName)) {
                        namedFields.put(longName, field);
                    }
                }
            }
        }

        // Sort the ordered fields
        orderedFields.sort((f1, f2) -> {
            Ordered f1o = f1.getAnnotation(Ordered.class);
            Ordered f2o = f2.getAnnotation(Ordered.class);
            return Integer.compare(f1o.order(), f2o.order());
        });
    }

    /**
     * Returns the current name.
     *
     * @param currentName The current name
     *
     * @return The current name
     */
    public ParseContext<T> setCurrentName(String currentName) {
        this.currentName = currentName;
        return this;
    }

    /**
     * Returns the current name.
     *
     * @return The current name
     */
    public T getInstance() {
        return instance;
    }

    /**
     * Returns the current ordered index.
     *
     * @return The current ordered index
     */
    public Stack<String> getQueue() {
        return queue;
    }

    /**
     * Sets the value of the current ordered property in the parse context to the given string value.
     *
     * @param stringValue The string value to parse and set
     * @throws ParseException If the value cannot be parsed
     */
    public void setOrderedValue(String stringValue) throws ParseException {
        int orderedIndex = currentOrderedIndex;
        try {
            Field field = orderedFields.get(currentOrderedIndex++);

            Ordered ordered = field.getAnnotation(Ordered.class);

            // Bean properties without this annotation are considered to be named properties not ordered properties
            if (ordered == null) {
                throw new NullPointerException(Ordered.class.getName() + " is missing. This should never happen");
            }

            TypeConverter<?> typeConverter = null;

            if (typeConverters.containsKey(field.getType())) {
                typeConverter = typeConverters.get(field.getType());
            } else if (!ordered.converter().equals(StringValueParser.class)) {
                try {
                    Class<? extends TypeConverter<?>> parserClass = ordered.converter();
                    typeConverter = parserClass.getConstructor().newInstance();
                } catch (Exception e) {
                    String message = format(
                            "Class %s must have a public no-arg constructor",
                            ordered.converter().getCanonicalName()
                    );
                    throw new RethrownException(message, e);
                }
            }

            Class<?> fieldType = field.getType();
            Object existingValue = ReflectionUtil.getFieldValue(field, instance);

            // Are we dealing with a collection?
            if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
                Object parsedValue = parse(stringValue, ordered.collectionType(), typeConverter);

                // Add a value to the collection
                existingValue = addToCollection(field, existingValue, fieldType, ordered.collectionType(), parsedValue);

                // Overwrite the collection in the instance
                ReflectionUtil.setFieldValue(field, instance, existingValue);
            } else {
                Object parsedValue = parse(stringValue, fieldType, typeConverter);
                ReflectionUtil.setFieldValue(field, instance, parsedValue);
            }
        } catch (RuntimeException | IllegalAccessException e) {
            String message = format("Failed to set value %s for position %s", stringValue, orderedIndex);
            throw new InaccessibleFieldException(message, e, instance.getClass());
        }
    }

    /**
     * This is broken out into its own function because the annotations required for silencing
     * the warnings cannot be used at the statement level.
     *
     * @param field The field which is being populated
     * @param collection The collection which will be added to
     * @param collectionType The type of the collection
     * @param elementType The type of the elements in the collection
     * @param parsedValue The value to add to the collection
     *
     * @return The updated collection
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Object addToCollection(Field field, Object collection, Class<?> collectionType, Class<?> elementType,
                                     Object parsedValue) {
        // Attempt to initialize the collection if it is null
        if (collection == null) {
            if (Collection.class.isAssignableFrom(collectionType)) {
                // Instantiate a new collection if possible. Note that this will not be possible in all cases
                //  because, outside built-in collections, it is not feasible to determine which concrete collection
                //  type to instantiate. In this case, an exception should be thrown.
                if (List.class.isAssignableFrom(collectionType)) {
                    collection = new ArrayList<>();
                } else if (Set.class.isAssignableFrom(collectionType)) {
                    collection = new HashSet<>();
                } else if (Queue.class.isAssignableFrom(collectionType)) {
                    collection = new LinkedList<>();
                } else {
                    throw new AssertionError(collectionType.getCanonicalName()
                            + " is not a supported collection type. To work around this, please initialize"
                            + " " + field + " with an empty collection.");
                }

                ((Collection) collection).add(parsedValue);
            } else if (collectionType.isArray()) {
                collection = Array.newInstance(elementType, 1);
                ((Object[]) collection)[0] = parsedValue;
            }
        } else {
            if (Collection.class.isAssignableFrom(collectionType)) {
                ((Collection) collection).add(parsedValue);
            } else if (collectionType.isArray()) {
                List<Object> tmpList = new ArrayList<>(Arrays.asList((Object[]) collection));
                tmpList.add(parsedValue);
                collection = tmpList.toArray(new Object[0]);
            }
        }

        // Return the updated collection
        return collection;
    }

    /**
     * Sets the value of the current named property in the parse context to the given value.
     *
     * @param value The value to set
     * @throws ParseException If the value cannot be parsed
     */
    public void setNamedValue(String value) throws ParseException {
        try {
            Field field = namedFields.get(currentName);

            // Quietly return if the field cannot be found. This may be the result of the user passing the wrong flag
            if (field == null) {
                return;
            }

            // TODO: Is there a way to do this without querying the annotation again?
            Arg named = field.getAnnotation(Arg.class);
            Class<?> fieldType = field.getType();
            TypeConverter<?> typeConverter = null;

            if (typeConverters.containsKey(fieldType)) {
                typeConverter = typeConverters.get(fieldType);
            } else if (named != null && !named.converter().equals(StringValueParser.class)) {
                typeConverter = ReflectionUtil.instantiate(named.converter());
            }

            Object existingValue = ReflectionUtil.getFieldValue(field, instance);

            // Are we dealing with a collection?
            if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
                // It is not possible to add an element to a collection without this annotation because we need to know
                // what type the collection contains
                if (named == null) {
                    String message = Arg.class.getName() + " is missing. This should never happen";
                    throw new NullPointerException(message);
                }

                Object parsedValue = parse(value, named.collectionType(), typeConverter);

                // Add a value to the collection
                existingValue = addToCollection(field, existingValue, fieldType, named.collectionType(), parsedValue);

                // Overwrite the collection in the instance
                ReflectionUtil.setFieldValue(field, instance, existingValue);
            } else {
                Object parsedValue = parse(value, field.getType(), typeConverter);
                ReflectionUtil.setFieldValue(field, instance, parsedValue);
            }
        } catch (RuntimeException | IllegalAccessException e) {
            String message = format("Failed to set value %s for flag %s", value, currentName);
            throw new InaccessibleFieldException(message, e, instance.getClass());
        }
    }

    /**
     * Parse the given value into an instance of the given field type.
     *
     * @param value The value to parse
     * @param fieldType The class type to parse the value into
     * @param typeConverter The type converter to use when parsing the value
     *
     * @return The parsed value
     * @throws ParseException If the value cannot be parsed
     */
    protected Object parse(String value, Class<?> fieldType, TypeConverter<?> typeConverter)
            throws ParseException {
        Object parsed = null;

        try {
            if (fieldType.equals(String.class)) {
                return value;
            }

            if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                parsed = Integer.parseInt(value);
            } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                parsed = Short.parseShort(value);
            } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                parsed = Long.parseLong(value);
            } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                parsed = Float.parseFloat(value);
            } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                parsed = Double.parseDouble(value);
            } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                parsed = Byte.parseByte(value);
            } else if (fieldType.equals(BigInteger.class)) {
                parsed = new BigInteger(value);
            } else if (fieldType.equals(BigDecimal.class)) {
                parsed = new BigDecimal(value);
            } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                // Throw an exception if the wrong number of characters are passed
                if (value == null || value.length() != 1) {
                    throw new ParseException(value, format("Value %s must contain exactly one character", value));
                }

                parsed = value.charAt(0);
            } else if (isBoolean(fieldType)) {
                if (value == null) {
                    return false;
                } else {
                    return Boolean.parseBoolean(value);
                }
            } else if (typeConverter != null) {
                try {
                    parsed = typeConverter.read(value);
                } catch (Exception e) {
                    throw new RethrownException(e);
                }
            } else {
                throw new UnsupportedTypeConversionException("Unsupported type: " + fieldType.getCanonicalName());
            }

            return parsed;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException(value, format(
                "Failed to parse string %s into an instance of class %s",
                value,
                fieldType
            ));
        }
    }

    /**
     * Returns true if the given class type is a boolean type.
     *
     * @param type The class type to check
     *
     * @return true if the given class type is a boolean type
     */
    private static boolean isBoolean(Class<?> type) {
        return type.equals(Boolean.class) || type.equals(boolean.class);
    }

    /**
     * Returns true if the current flag is a boolean flag.
     *
     * @return true if the current flag is a boolean flag
     */
    public boolean isBoolean() {
        Field field = namedFields.get(currentName);

        if (field == null) {
            return false;
        }

        return isBoolean(field.getType());
    }

    /**
     * Returns the list of required fields.
     *
     * @return The list of required fields
     */
    public List<Field> getRequiredFields() {
        return requiredFields;
    }

    /**
     * Returns true if the given token is a help token.
     *
     * @param token The token to check
     *
     * @return true if the given token is a help token
     */
    public boolean isHelpToken(String token) {
        return helpTokens.contains(token);
    }

    /**
     * This class is used to dynamically get the default values of annotations in cases where classes have
     * not been annotated.
     */
    @Command
    private static class AnnotationDefaults {

    }
}
