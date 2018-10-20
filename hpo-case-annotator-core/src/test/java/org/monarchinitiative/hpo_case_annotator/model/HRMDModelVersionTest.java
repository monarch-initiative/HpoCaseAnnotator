package org.monarchinitiative.hpo_case_annotator.model;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore // deprecated
public class HRMDModelVersionTest {

    /**
     * Test normal input.
     */
    @Test
    public void testCreation() throws Exception {
        HRMDModelVersion first = new HRMDModelVersion(75, 87, 10);
        assertEquals(75, first.getMajor());
        assertEquals(87, first.getMinor());
        assertEquals(10, first.getPatch());
    }


    /**
     * Test creation using proper string as input.
     */
    @Test
    public void testCreationFromString() throws Exception {
        String one = "v25.45.26";
        HRMDModelVersion first = new HRMDModelVersion(one);
        assertEquals(25, first.getMajor());
        assertEquals(45, first.getMinor());
        assertEquals(26, first.getPatch());
    }


    /**
     * Test that exception is thrown when using negative major number.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeMajor() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion(-1, 2, 33);
    }


    /**
     * Test that exception is thrown when using negative minor number.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeMinor() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion(19, -2, 33);
    }


    /**
     * Test that exception is thrown when using negative patch number.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativePatch() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion(14, 25, -33);
    }


    /**
     * Test that exception is thrown when using zero before major version number.
     */
    @Test(expected = IllegalArgumentException.class)
    public void zeroBeforeMajor() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion("v054.54.44");
    }


    /**
     * Test that exception is thrown when using improper String as input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void incorrectString() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion("vdf.sa");
    }


    /**
     * Test that exception is thrown when using zero before major version number.
     */
    @Test(expected = IllegalArgumentException.class)
    public void tooLittleFields() throws Exception {
        HRMDModelVersion illegal = new HRMDModelVersion("v12.32");
    }


    /**
     * Test implementation of {@link Comparable} interface.
     */
    @Test
    public void testComparison() throws Exception {
        HRMDModelVersion one, two, three, four, five, six, seven, eight;
        one = new HRMDModelVersion(2, 33, 45);
        two = new HRMDModelVersion(2, 33, 44);
        three = new HRMDModelVersion(2, 33, 46);
        four = new HRMDModelVersion(2, 32, 44);
        five = new HRMDModelVersion(2, 34, 44);
        six = new HRMDModelVersion(1, 33, 44);
        seven = new HRMDModelVersion(3, 33, 44);
        eight = new HRMDModelVersion(2, 33, 45);

        assertTrue(one.compareTo(two) > 0);
        assertTrue(one.compareTo(three) < 0);
        assertTrue(one.compareTo(four) > 0);
        assertTrue(one.compareTo(five) < 0);
        assertTrue(one.compareTo(six) > 0);
        assertTrue(one.compareTo(seven) < 0);
        assertTrue(one.compareTo(eight) == 0);
    }


    /**
     * Test implementation of equals and hashCode methods.
     */
    @Test
    public void testEqualsAndHashCode() throws Exception {
        HRMDModelVersion one, two;
        one = new HRMDModelVersion(2, 33, 44);
        two = new HRMDModelVersion(2, 33, 44);

        assertEquals(one.hashCode(), two.hashCode());
        assertTrue(one.equals(two));
    }
}