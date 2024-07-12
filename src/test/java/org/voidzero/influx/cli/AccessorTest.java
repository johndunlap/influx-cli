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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.Test;

/**
 * Tests that getting and setting values in Java objects via reflection works as expected.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class AccessorTest {

    @Test
    public void testFindSetterMethod() throws NoSuchFieldException {
        Field field = NamedConfig.class.getDeclaredField("booleanValue");
        Method setterMethod = ReflectionUtil.findSetterMethod(field);
        assertNotNull(setterMethod);
        assertEquals("setBooleanValue", setterMethod.getName());
    }

    @Test
    public void testFindGetterMethod() throws NoSuchFieldException {
        Field field = NamedConfig.class.getDeclaredField("booleanValue");
        Method getterMethod = ReflectionUtil.findGetterMethod(field);
        assertNotNull(getterMethod);
        assertEquals("getBooleanValue", getterMethod.getName());
    }

    @Test
    public void testGetFieldWithPrivateGetterPrivateField()
            throws NoSuchFieldException, IllegalAccessException {
        PrivateGetterPrivateField instance = new PrivateGetterPrivateField("test");
        Field field = instance.getClass().getDeclaredField("privateField");

        assertFalse(instance.wasGetterCalled());
        String value = (String) ReflectionUtil.getFieldValue(field, instance);
        assertEquals("test", value);
        assertTrue(instance.wasGetterCalled());
    }

    @Test
    public void testGetFieldWithPrivateGetterPublicField()
            throws NoSuchFieldException, IllegalAccessException {
        PrivateGetterPublicField instance = new PrivateGetterPublicField("test");
        Field field = instance.getClass().getDeclaredField("publicField");

        assertFalse(instance.wasGetterCalled());
        String value = (String) ReflectionUtil.getFieldValue(field, instance);
        assertEquals("test", value);
        assertFalse(instance.wasGetterCalled());
    }

    @Test
    public void testGetFieldWithPublicGetterPrivateField()
            throws NoSuchFieldException, IllegalAccessException {
        PublicGetterPrivateField instance = new PublicGetterPrivateField("test");
        Field field = instance.getClass().getDeclaredField("privateField");

        assertFalse(instance.wasGetterCalled());
        String value = (String) ReflectionUtil.getFieldValue(field, instance);
        assertEquals("test", value);
        assertTrue(instance.wasGetterCalled());
    }

    @Test
    public void testGetFieldWithPublicGetterPublicField()
            throws NoSuchFieldException, IllegalAccessException {
        PublicGetterPublicField instance = new PublicGetterPublicField("test");
        Field field = instance.getClass().getDeclaredField("publicField");

        assertFalse(instance.wasGetterCalled());
        String value = (String) ReflectionUtil.getFieldValue(field, instance);
        assertEquals("test", value);
        assertTrue(instance.wasGetterCalled());
    }

    @Test
    public void testSetFieldWithPrivateSetterPrivateField()
            throws NoSuchFieldException, IllegalAccessException {
        PrivateSetterPrivateField instance = new PrivateSetterPrivateField();
        Field field = instance.getClass().getDeclaredField("privateField");

        assertFalse(instance.wasSetterCalled());
        assertNull(instance.getPrivateField());
        ReflectionUtil.setFieldValue(field, instance, "test");
        assertTrue(instance.wasSetterCalled());
        assertEquals("test", instance.getPrivateField());
    }

    @Test
    public void testSetFieldWithPrivateSetterPublicField()
            throws NoSuchFieldException, IllegalAccessException {
        PrivateSetterPublicField instance = new PrivateSetterPublicField();
        Field field = instance.getClass().getDeclaredField("publicField");

        assertFalse(instance.wasSetterCalled());
        assertNull(instance.getPublicField());
        ReflectionUtil.setFieldValue(field, instance, "test");
        assertFalse(instance.wasSetterCalled());
        assertEquals("test", instance.getPublicField());
    }

    @Test
    public void testSetFieldWithPublicSetterPrivateField()
            throws NoSuchFieldException, IllegalAccessException {
        PublicSetterPrivateField instance = new PublicSetterPrivateField();
        Field field = instance.getClass().getDeclaredField("privateField");

        assertFalse(instance.wasSetterCalled());
        assertNull(instance.getPrivateField());
        ReflectionUtil.setFieldValue(field, instance, "test");
        assertTrue(instance.wasSetterCalled());
        assertEquals("test", instance.getPrivateField());
    }

    @Test
    public void testSetFieldWithPublicSetterPublicField()
            throws NoSuchFieldException, IllegalAccessException {
        PublicSetterPublicField instance = new PublicSetterPublicField();
        Field field = instance.getClass().getDeclaredField("publicField");

        assertFalse(instance.wasSetterCalled());
        assertNull(instance.getPublicField());
        ReflectionUtil.setFieldValue(field, instance, "test");
        assertTrue(instance.wasSetterCalled());
        assertEquals("test", instance.getPublicField());
    }

    private static class NamedConfig {
        private Boolean booleanValue;

        public NamedConfig() {
        }

        public Boolean getBooleanValue() {
            return booleanValue;
        }

        public NamedConfig setBooleanValue(Boolean booleanValue) {
            this.booleanValue = booleanValue;
            return this;
        }
    }

    private static class PrivateGetterPrivateField {
        private String privateField;
        private boolean getterCalled = false;

        public PrivateGetterPrivateField(String privateField) {
            setPrivateField(privateField);
        }

        private String getPrivateField() {
            getterCalled = true;
            return privateField;
        }

        public PrivateGetterPrivateField setPrivateField(String privateField) {
            this.privateField = privateField;
            return this;
        }

        public boolean wasGetterCalled() {
            return getterCalled;
        }
    }

    private static class PrivateGetterPublicField {
        public String publicField;
        private boolean getterCalled = false;

        public PrivateGetterPublicField(String publicField) {
            setPublicField(publicField);
        }

        private String getPublicField() {
            getterCalled = true;
            return publicField;
        }

        public PrivateGetterPublicField setPublicField(String publicField) {
            this.publicField = publicField;
            return this;
        }

        public boolean wasGetterCalled() {
            return getterCalled;
        }
    }

    private static class PrivateSetterPrivateField {
        private String privateField;
        private boolean setterCalled = false;

        public String getPrivateField() {
            return privateField;
        }

        private PrivateSetterPrivateField setPrivateField(String privateField) {
            this.privateField = privateField;
            setterCalled = true;
            return this;
        }

        public boolean wasSetterCalled() {
            return setterCalled;
        }

        public PrivateSetterPrivateField setSetterCalled(boolean setterCalled) {
            this.setterCalled = setterCalled;
            return this;
        }
    }

    private static class PrivateSetterPublicField {
        public String publicField;
        private boolean setterCalled = false;

        public String getPublicField() {
            return publicField;
        }

        private PrivateSetterPublicField setPublicField(String publicField) {
            this.publicField = publicField;
            setterCalled = true;
            return this;
        }

        public boolean wasSetterCalled() {
            return setterCalled;
        }

        public PrivateSetterPublicField setSetterCalled(boolean setterCalled) {
            this.setterCalled = setterCalled;
            return this;
        }
    }

    private static class PublicGetterPrivateField {
        private String privateField;
        private boolean getterCalled = false;

        public PublicGetterPrivateField(String privateField) {
            setPrivateField(privateField);
        }

        public String getPrivateField() {
            getterCalled = true;
            return privateField;
        }

        public PublicGetterPrivateField setPrivateField(String privateField) {
            this.privateField = privateField;
            return this;
        }

        public boolean wasGetterCalled() {
            return getterCalled;
        }

        public PublicGetterPrivateField setGetterCalled(boolean getterCalled) {
            this.getterCalled = getterCalled;
            return this;
        }
    }

    private static class PublicGetterPublicField {
        private String publicField;
        private boolean getterCalled = false;

        public PublicGetterPublicField(String publicField) {
            setPublicField(publicField);
        }

        public String getPublicField() {
            getterCalled = true;
            return publicField;
        }

        public PublicGetterPublicField setPublicField(String publicField) {
            this.publicField = publicField;
            return this;
        }

        public boolean wasGetterCalled() {
            return getterCalled;
        }

        public PublicGetterPublicField setGetterCalled(boolean getterCalled) {
            this.getterCalled = getterCalled;
            return this;
        }
    }

    private static class PublicSetterPrivateField {
        private String privateField;
        private boolean setterCalled = false;


        public String getPrivateField() {
            return privateField;
        }

        public PublicSetterPrivateField setPrivateField(String privateField) {
            this.privateField = privateField;
            setterCalled = true;
            return this;
        }

        public boolean wasSetterCalled() {
            return setterCalled;
        }

        public PublicSetterPrivateField setSetterCalled(boolean setterCalled) {
            this.setterCalled = setterCalled;
            return this;
        }
    }

    private static class PublicSetterPublicField {
        public String publicField;
        private boolean setterCalled = false;

        public String getPublicField() {
            return publicField;
        }

        public PublicSetterPublicField setPublicField(String publicField) {
            this.publicField = publicField;
            setterCalled = true;
            return this;
        }

        public boolean wasSetterCalled() {
            return setterCalled;
        }

        public PublicSetterPublicField setSetterCalled(boolean setterCalled) {
            this.setterCalled = setterCalled;
            return this;
        }
    }
}
