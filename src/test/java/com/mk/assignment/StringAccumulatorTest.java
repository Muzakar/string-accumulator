package com.mk.assignment;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link StringAccumulator}
 */
public class StringAccumulatorTest {

    private static StringAccumulator stringAccumulator;

    @BeforeClass
    public static void setUp() {
        stringAccumulator = new StringAccumulator();
    }

    @Test
    public void testEmptyAndNullString() {
        assertEquals(0, stringAccumulator.add(""));
        assertEquals(0, stringAccumulator.add(null));
    }

    @Test
    public void testWithoutDelimiters() {
        assertEquals(0, stringAccumulator.add("0"));
        assertEquals(1, stringAccumulator.add("1"));
        assertEquals(3, stringAccumulator.add("1,2"));
        assertEquals(6, stringAccumulator.add("1\n2,3"));
        assertEquals(6, stringAccumulator.add("1-2-3"));
        assertEquals(6, stringAccumulator.add("1@2-3"));
        assertEquals(6, stringAccumulator.add("1\n2,*3"));
        assertEquals(26, stringAccumulator.add("1\n\n\n2,*3,8***9%3"));
        assertEquals(26, stringAccumulator.add("1\n\n\n2\n\n\n3,8***9@3"));
        assertEquals(31, stringAccumulator.add("1\n\n\n2\n\n\n3,8*#*9*#*7*#*1"));
        assertEquals(3100, stringAccumulator.add("100\n\n\n200*#*300,800*#*900*#*700\n\n\n100"));
    }

    @Test
    public void testWithDelimiters() {
        assertEquals(6, stringAccumulator.add("//*|%\n1*2%3"));
        assertEquals(6, stringAccumulator.add("//***\n1***2***3"));
        assertEquals(37, stringAccumulator.add("//##|,|*#*\n1##2##3,8*#*9*#*7*#*1,6"));
        assertEquals(3700, stringAccumulator.add("//##|,|*#*\n100##200##300,800*#*900*#*700*#*100,600"));
        assertEquals(3700, stringAccumulator.add("//##|,|*#*\n100##200*#*300,800*#*900##700*#*100,600"));
        assertEquals(3700, stringAccumulator.add("//##|\n\n\n|*#*\n100##200*#*300\n\n\n800*#*900##700*#*100\n\n\n600"));
    }

    @Test
    public void testWithNegativeNumber() {
        try {
            stringAccumulator.add("//*|%\n1*-2%3");
        } catch (Exception e) {
            assertEquals("negatives not allowed : -2", e.getMessage());
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        try {
            stringAccumulator.add("//##|\n\n\n|*#*\n1000##-2000*#*300\n\n\n800*#*900##-700*#*100\n\n\n600");
        } catch (Exception e) {
            assertEquals("negatives not allowed : -2000,-700", e.getMessage());
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        try {
            stringAccumulator.add("100\n\n\n200*#*300,-800*#*900*#*700\n\n\n-100");
        } catch (Exception e) {
            assertEquals("negatives not allowed : -100,-800", e.getMessage());
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        try {
            stringAccumulator.add("100---200*#*300--800*#*900*#*700\n\n\n-100");
        } catch (Exception e) {
            assertEquals("negatives not allowed : -200,-800,-100", e.getMessage());
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testWithNumberBiggerThan1000() {
        assertEquals(4, stringAccumulator.add("//*|%\n1*1001%3"));
        assertEquals(4600, stringAccumulator.add("//##|\n\n\n|*#*\n1000##200*#*300\n\n\n800*#*900##700*#*100\n\n\n600"));
        assertEquals(4400, stringAccumulator.add("//##|\n\n\n|*#*\n1000##2000*#*300\n\n\n800*#*900##700*#*100\n\n\n600"));
    }
}