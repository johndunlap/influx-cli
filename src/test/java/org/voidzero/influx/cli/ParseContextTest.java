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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.exception.HelpException;
import org.voidzero.influx.cli.exception.ParseException;
import org.voidzero.influx.cli.exception.RethrownException;
import org.voidzero.influx.cli.exception.UnsupportedTypeConversionException;

/**
 * Tests for {@link ParseContext}.
 */
public class ParseContextTest {

    private static final String PI = "3.1415926535897932384626433832795028841971693993751058209749445923";
    private static final String FIBONACCI = "0112358132134558914423337761098715972584418167651094617711";

    @Test
    public void testBigIntegerBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--big-integer", FIBONACCI};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(new BigInteger(FIBONACCI), supportedTypes.getBigInteger());
    }

    @Test
    public void testBigDecimalBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--big-decimal", PI};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(new BigDecimal(PI), supportedTypes.getBigDecimal());
    }

    @Test
    public void testIntegerBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--integer-reference", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Integer.valueOf(42), supportedTypes.getIntegerReference());
    }

    @Test
    public void testIntegerPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--integer-primitive", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(42, supportedTypes.getIntegerPrimitive());
    }

    @Test
    public void testShortBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--short-reference", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Short.valueOf((short) 42), supportedTypes.getShortReference());
    }

    @Test
    public void testShortPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--short-primitive", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals((short) 42, supportedTypes.getShortPrimitive());
    }

    @Test
    public void testLongBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--long-reference", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Long.valueOf(42), supportedTypes.getLongReference());
    }

    @Test
    public void testLongPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--long-primitive", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(42L, supportedTypes.getLongPrimitive());
    }

    @Test
    public void testFloatBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--float-reference", "42.0"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Float.valueOf(42.0f), supportedTypes.getFloatReference());
    }

    @Test
    public void testFloatPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--float-primitive", "42.0"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(42.0f, supportedTypes.getFloatPrimitive(), 0.0f);
    }

    @Test
    public void testDoubleBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--double-reference", "42.0"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Double.valueOf(42.0), supportedTypes.getDoubleReference());
    }

    @Test
    public void testDoublePrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--double-primitive", "42.0"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(42.0, supportedTypes.getDoublePrimitive(), 0.0);
    }

    @Test
    public void testBooleanBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--boolean-reference"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Boolean.TRUE, supportedTypes.getBooleanReference());
    }

    @Test
    public void testBooleanPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--boolean-primitive"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertTrue(supportedTypes.isBooleanPrimitive());
    }

    @Test
    public void testCharacterBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--character-reference", "a"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Character.valueOf('a'), supportedTypes.getCharacterReference());
    }

    @Test
    public void testCharacterPrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--character-primitive", "a"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals('a', supportedTypes.getCharacterPrimitive());
    }

    @Test
    public void testStringBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--string", "hello"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals("hello", supportedTypes.getString());
    }

    @Test
    public void testByteBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--byte-reference", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(Byte.valueOf((byte) 42), supportedTypes.getByteReference());
    }

    @Test
    public void testBytePrimitiveBinding() throws ParseException, HelpException {
        String[] args = new String[]{"--byte-primitive", "42"};
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals((byte) 42, supportedTypes.getBytePrimitive());
    }

    @Test
    public void testAllSupportedTypes() throws ParseException, HelpException {
        String[] args = new String[]{
            "--big-integer", FIBONACCI,
            "--big-decimal", PI,
            "--integer-reference", "42",
            "--integer-primitive", "42",
            "--short-reference", "42",
            "--short-primitive", "42",
            "--long-reference", "42",
            "--long-primitive", "42",
            "--float-reference", "42.0",
            "--float-primitive", "42.0",
            "--double-reference", "42.0",
            "--double-primitive", "42.0",
            "--boolean-reference",
            "--boolean-primitive",
            "--character-reference", "a",
            "--character-primitive", "a",
            "--string", "hello",
            "--byte-reference", "42",
            "--byte-primitive", "42"
        };
        SupportedTypes supportedTypes = (SupportedTypes) new InfluxCli().bind(SupportedTypes.class, args);
        assertEquals(new BigInteger(FIBONACCI), supportedTypes.getBigInteger());
        assertEquals(new BigDecimal(PI), supportedTypes.getBigDecimal());
        assertEquals(Integer.valueOf(42), supportedTypes.getIntegerReference());
        assertEquals(42, supportedTypes.getIntegerPrimitive());
        assertEquals(Short.valueOf((short) 42), supportedTypes.getShortReference());
        assertEquals((short) 42, supportedTypes.getShortPrimitive());
        assertEquals(Long.valueOf(42), supportedTypes.getLongReference());
        assertEquals(42L, supportedTypes.getLongPrimitive());
        assertEquals(Float.valueOf(42.0f), supportedTypes.getFloatReference());
        assertEquals(42.0f, supportedTypes.getFloatPrimitive(), 0.0f);
        assertEquals(Double.valueOf(42.0), supportedTypes.getDoubleReference());
        assertEquals(42.0, supportedTypes.getDoublePrimitive(), 0.0);
        assertEquals(Boolean.TRUE, supportedTypes.getBooleanReference());
        assertTrue(supportedTypes.isBooleanPrimitive());
        assertEquals(Character.valueOf('a'), supportedTypes.getCharacterReference());
        assertEquals('a', supportedTypes.getCharacterPrimitive());
        assertEquals("hello", supportedTypes.getString());
        assertEquals(Byte.valueOf((byte) 42), supportedTypes.getByteReference());
        assertEquals((byte) 42, supportedTypes.getBytePrimitive());
    }

    @Test(expected = RethrownException.class)
    public void testTypeConverterThrowsParseException() throws ParseException, HelpException {
        new InfluxCli().bind(CustomTypeConfig.class, new String[]{"--custom-type", "42"});
    }

    @Test(expected = UnsupportedTypeConversionException.class)
    public void testUnsupportedTypeException() throws ParseException, HelpException {
        new InfluxCli().bind(CustomTypeConfig.class, new String[] {"--unsupported-type", "42"});
    }

    @Test(expected = ParseException.class)
    public void testTooManyCharacters() throws ParseException, HelpException {
        new InfluxCli().bind(SupportedTypes.class, new String[]{"--character-reference", "abc"});
    }

    @Test
    public void testParseNullBooleanValue() throws ParseException, HelpException {
        ParseContext<SupportedTypes> parseContext = new ParseContext<>(
                SupportedTypes.class,
                new String[]{},
                null
        );

        Boolean value = (Boolean) parseContext.parse(null, Boolean.class, null);
        assertFalse(value);
    }

    private static class SupportedTypes {
        private BigInteger bigInteger;

        private BigDecimal bigDecimal;

        private Integer integerReference;

        private int integerPrimitive;

        private Short shortReference;

        private short shortPrimitive;

        private Long longReference;

        private long longPrimitive;

        private Float floatReference;

        private float floatPrimitive;

        private Double doubleReference;

        private double doublePrimitive;

        private Boolean booleanReference;

        private boolean booleanPrimitive;

        private Character characterReference;

        private char characterPrimitive;

        private String string;

        private Byte byteReference;

        private byte bytePrimitive;

        public SupportedTypes() {
        }

        public BigInteger getBigInteger() {
            return bigInteger;
        }

        public SupportedTypes setBigInteger(BigInteger bigInteger) {
            this.bigInteger = bigInteger;
            return this;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public SupportedTypes setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
            return this;
        }

        public Integer getIntegerReference() {
            return integerReference;
        }

        public SupportedTypes setIntegerReference(Integer integerReference) {
            this.integerReference = integerReference;
            return this;
        }

        public int getIntegerPrimitive() {
            return integerPrimitive;
        }

        public SupportedTypes setIntegerPrimitive(int integerPrimitive) {
            this.integerPrimitive = integerPrimitive;
            return this;
        }

        public Short getShortReference() {
            return shortReference;
        }

        public SupportedTypes setShortReference(Short shortReference) {
            this.shortReference = shortReference;
            return this;
        }

        public short getShortPrimitive() {
            return shortPrimitive;
        }

        public SupportedTypes setShortPrimitive(short shortPrimitive) {
            this.shortPrimitive = shortPrimitive;
            return this;
        }

        public Long getLongReference() {
            return longReference;
        }

        public SupportedTypes setLongReference(Long longReference) {
            this.longReference = longReference;
            return this;
        }

        public long getLongPrimitive() {
            return longPrimitive;
        }

        public SupportedTypes setLongPrimitive(long longPrimitive) {
            this.longPrimitive = longPrimitive;
            return this;
        }

        public Float getFloatReference() {
            return floatReference;
        }

        public SupportedTypes setFloatReference(Float floatReference) {
            this.floatReference = floatReference;
            return this;
        }

        public float getFloatPrimitive() {
            return floatPrimitive;
        }

        public SupportedTypes setFloatPrimitive(float floatPrimitive) {
            this.floatPrimitive = floatPrimitive;
            return this;
        }

        public Double getDoubleReference() {
            return doubleReference;
        }

        public SupportedTypes setDoubleReference(Double doubleReference) {
            this.doubleReference = doubleReference;
            return this;
        }

        public double getDoublePrimitive() {
            return doublePrimitive;
        }

        public SupportedTypes setDoublePrimitive(double doublePrimitive) {
            this.doublePrimitive = doublePrimitive;
            return this;
        }

        public Boolean getBooleanReference() {
            return booleanReference;
        }

        public SupportedTypes setBooleanReference(Boolean booleanReference) {
            this.booleanReference = booleanReference;
            return this;
        }

        public boolean isBooleanPrimitive() {
            return booleanPrimitive;
        }

        public SupportedTypes setBooleanPrimitive(boolean booleanPrimitive) {
            this.booleanPrimitive = booleanPrimitive;
            return this;
        }

        public Character getCharacterReference() {
            return characterReference;
        }

        public SupportedTypes setCharacterReference(Character characterReference) {
            this.characterReference = characterReference;
            return this;
        }

        public char getCharacterPrimitive() {
            return characterPrimitive;
        }

        public SupportedTypes setCharacterPrimitive(char characterPrimitive) {
            this.characterPrimitive = characterPrimitive;
            return this;
        }

        public String getString() {
            return string;
        }

        public SupportedTypes setString(String string) {
            this.string = string;
            return this;
        }

        public Byte getByteReference() {
            return byteReference;
        }

        public SupportedTypes setByteReference(Byte byteReference) {
            this.byteReference = byteReference;
            return this;
        }

        public byte getBytePrimitive() {
            return bytePrimitive;
        }

        public SupportedTypes setBytePrimitive(byte bytePrimitive) {
            this.bytePrimitive = bytePrimitive;
            return this;
        }
    }

    private static class CustomTypeConfig {
        @Arg(converter = TypeConverterErrorConverter.class)
        private CustomType customType;

        private UnsupportedType unsupportedType;

        public CustomTypeConfig() {
        }

        public CustomType getCustomType() {
            return customType;
        }

        public CustomTypeConfig setCustomType(CustomType customType) {
            this.customType = customType;
            return this;
        }

        public UnsupportedType getUnsupportedType() {
            return unsupportedType;
        }

        public CustomTypeConfig setUnsupportedType(UnsupportedType unsupportedType) {
            this.unsupportedType = unsupportedType;
            return this;
        }
    }

    private static class CustomType {
        String internalRepresentation;

        public CustomType() {
        }

        public String getInternalRepresentation() {
            return internalRepresentation;
        }

        public CustomType setInternalRepresentation(String internalRepresentation) {
            this.internalRepresentation = internalRepresentation;
            return this;
        }
    }

    private static class UnsupportedType {

    }

    private static class TypeConverterErrorConverter implements TypeConverter<Integer> {

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public Integer read(String value) throws ParseException {
            throw new ParseException("error");
        }

        @Override
        public String write(Integer value) throws ParseException {
            throw new ParseException("error");
        }
    }
}
