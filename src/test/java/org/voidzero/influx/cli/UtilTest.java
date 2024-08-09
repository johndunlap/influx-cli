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

import org.junit.Test;

/**
 * Tests for utility methods.
 *
 * @author <a href="mailto:john.david.dunlap@gmail.com">John Dunlap</a>
 */
public class UtilTest {

    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc nec lacus"
            + " eu lectus ultricies vehicula. Nullam faucibus nibh eu neque gravida, ac cursus leo posuere. Nam"
            + " imperdiet consequat nisi, eu luctus sem hendrerit eget. Cras ornare sagittis sagittis. Suspendisse at"
            + " tellus tellus. Cras maximus efficitur tincidunt. Nunc tristique hendrerit lorem porttitor vulputate."
            + " Fusce condimentum dui eget justo interdum finibus. Vestibulum tincidunt sit amet justo vitae"
            + " pellentesque. Sed accumsan eu nunc id mattis. In nec nisi venenatis ex porttitor sagittis eu sed nisi.";

    @Test
    public void testCamelCaseToHyphenCaseMethod() {
        assertEquals("thisIsATest", Parser.hyphenCaseToCamelCase("this-is-a-test"));
    }

    @Test
    public void testHyphenCaseToCamelCaseMethod() {
        assertEquals("this-is-a-test", Parser.camelCaseToHyphenCase("thisIsATest"));
    }

    @Test
    public void testCamelCaseToHyphenCaseWithSingleWord() {
        assertEquals("second", Parser.camelCaseToHyphenCase("second"));
    }

    @Test
    public void testLineWrapMethodWith80MaxLength() {
        String actual = Parser.wordWrap(LOREM_IPSUM, 80);
        String expected = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc nec lacus eu\n"
                + "lectus ultricies vehicula. Nullam faucibus nibh eu neque gravida, ac cursus leo\n"
                + "posuere. Nam imperdiet consequat nisi, eu luctus sem hendrerit eget. Cras\n"
                + "ornare sagittis sagittis. Suspendisse at tellus tellus. Cras maximus efficitur\n"
                + "tincidunt. Nunc tristique hendrerit lorem porttitor vulputate. Fusce\n"
                + "condimentum dui eget justo interdum finibus. Vestibulum tincidunt sit amet\n"
                + "justo vitae pellentesque. Sed accumsan eu nunc id mattis. In nec nisi venenatis\n"
                + "ex porttitor sagittis eu sed nisi.";
        assertEquals(expected, actual);
    }

    @Test
    public void testLineWrapMethodWith40MaxLength() {
        String actual = Parser.wordWrap(LOREM_IPSUM, 40);
        String expected = "Lorem ipsum dolor sit amet, consectetur\n"
                + "adipiscing elit. Nunc nec lacus eu\n"
                + "lectus ultricies vehicula. Nullam\n"
                + "faucibus nibh eu neque gravida, ac\n"
                + "cursus leo posuere. Nam imperdiet\n"
                + "consequat nisi, eu luctus sem hendrerit\n"
                + "eget. Cras ornare sagittis sagittis.\n"
                + "Suspendisse at tellus tellus. Cras\n"
                + "maximus efficitur tincidunt. Nunc\n"
                + "tristique hendrerit lorem porttitor\n"
                + "vulputate. Fusce condimentum dui eget\n"
                + "justo interdum finibus. Vestibulum\n"
                + "tincidunt sit amet justo vitae\n"
                + "pellentesque. Sed accumsan eu nunc id\n"
                + "mattis. In nec nisi venenatis ex\n"
                + "porttitor sagittis eu sed nisi.";
        assertEquals(expected, actual);
    }
}
