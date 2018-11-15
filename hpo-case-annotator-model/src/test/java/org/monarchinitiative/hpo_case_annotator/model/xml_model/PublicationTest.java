package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PublicationTest {

    /**
     * Test correct behavior of {@link Publication#isEmpty()} method.
     */
    @Test
    public void isEmptyTest() throws Exception {
        Publication empty = new Publication();
        assertTrue(empty.isEmpty());

        Publication full = new Publication("Author F., Author S.", "Some random fun", "Fun J", "2017", "21",
                "554-78", "4556775");
        assertFalse(full.isEmpty());

        empty.setJournal("Fun J");
        assertFalse(empty.isEmpty());
    }


    /**
     * Test correct behavior of {@link Publication#equals(Object)} method.
     */
    @Test
    public void equalsTest() throws Exception {
        Publication one, two, three;
        one = new Publication("Author F., Author S.", "Some random fun", "Fun J", "2017", "21",
                "554-78", "4556775");
        two = new Publication("Author F., Author S.", "Some random fun", "Fun J", "2017", "21",
                "554-78", "4556775");
        three = new Publication("Other A.", "Not just some random fun", "Fun J", "2017", "21",
                "554-78", "4556788");
        assertEquals(one, two);
        assertEquals(two, one);
        assertNotEquals(one, three);
        assertNotEquals(three, one);
    }
}