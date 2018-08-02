package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.HpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.Publication;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Test behaviour of {@link ShowEditPublicationController}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 1.0.1
 * @since 1.0
 */
@Ignore("Gui tests are ignored for now")
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({HpoCaseAnnotatorModule.class})
public class ShowEditPublicationControllerTest extends ApplicationTest {

    private static final String AUTHORS = "Berg MA, Guevara-Aguirre J, Rosenbloom AL, Rosenfeld RG, Francke U";

    private static final String TITLE = "Mutation creating a new splice site in the growth hormone receptor genes of 37 " +
            "Ecuadorean patients with Laron syndrome.";

    private static final String JOURNAL = "Hum Mutat";

    private static final String YEAR = "1992";

    private static final String VOLUME = "1(1)";

    private static final String PAGES = "24-32";

    private static final String PMID = "1284474";


    @Inject
    private ShowEditPublicationController controller;


    @BeforeClass
    public static void setUp() throws Exception {
        System.setProperty("headless", "true"); // set this to false if you want to see the robot in action.
        System.setProperty("testfx.headless", "true"); // REQUIRED
        System.setProperty("testfx.robot", "glass"); // REQUIRED
        System.setProperty("prism.order", "sw"); // REQUIRED
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.clearProperty("headless");
        System.clearProperty("testfx.robot");
        System.clearProperty("testfx.headless");
        System.clearProperty("prism.order");
    }


    /**
     * Test correct behaviour when {@link Publication} with null attributes is set for viewing/editing.
     */
    @Test
    public void testDisplayingNullValues() throws Exception {
//        controller.setPublication(new Publication(null, null, null, null, null, null, null));
//        assertEquals(null, ((TextField) lookup("#titleTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#authorsTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#journalTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#yearTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#volumeTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#pagesTextField").query()).getText());
//        assertEquals(null, ((TextField) lookup("#pmidTextField").query()).getText());
    }


    /**
     * Test when no {@link Publication} is set. The fields are expected to be initialized to null.
     */
    @Test
    public void testInitialization() throws Exception {
        assertEquals(null, ((TextField) lookup("#titleTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#authorsTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#journalTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#yearTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#volumeTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#pagesTextField").query()).getText());
        assertEquals(null, ((TextField) lookup("#pmidTextField").query()).getText());
    }


    /**
     * Test bindings between GUI elements and publication attributes. The elements are expected to be populated with
     * attributes of the publication.
     */
    @Test
    public void setPublication() throws Exception {
//        Publication publication = new Publication(AUTHORS, TITLE, JOURNAL, YEAR, VOLUME, PAGES, PMID);
//        controller.setPublication(publication);
//        assertEquals(TITLE, ((TextField) lookup("#titleTextField").query()).getText());
//        assertEquals(AUTHORS, ((TextField) lookup("#authorsTextField").query()).getText());
//        assertEquals(JOURNAL, ((TextField) lookup("#journalTextField").query()).getText());
//        assertEquals(YEAR, ((TextField) lookup("#yearTextField").query()).getText());
//        assertEquals(VOLUME, ((TextField) lookup("#volumeTextField").query()).getText());
//        assertEquals(PAGES, ((TextField) lookup("#pagesTextField").query()).getText());
//        assertEquals(PMID, ((TextField) lookup("#pmidTextField").query()).getText());
    }


    /**
     * Change content of all text fields and check that attributes of {@link Publication} have been changed as well.
     */
    @Test
    public void testUpdatePublication() throws Exception {
//        Publication publication = new Publication(AUTHORS, TITLE, JOURNAL, YEAR, VOLUME, PAGES, PMID);
//        controller.setPublication(publication);
//        controller.setPublication(publication);
//
//        TextField tested = lookup("#titleTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("New title");
//
//        tested = lookup("#authorsTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("Other authors");
//
//        tested = lookup("#journalTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("Nat Genet");
//
//        tested = lookup("#yearTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("2015");
//
//        tested = lookup("#volumeTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("2(2)");
//
//        tested = lookup("#pagesTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("33-36");
//
//        tested = lookup("#pmidTextField").query();
//        clickOn(tested).type(KeyCode.HOME).push(KeyCode.SHIFT, KeyCode.END).write("1234567");
//
//        Publication actual = controller.getPublication();
//        assertThat(actual.getTitle(), is("New title"));
//        assertThat(actual.getAuthorlist(), is("Other authors"));
//        assertThat(actual.getJournal(), is("Nat Genet"));
//        assertThat(actual.getYear(), is("2015"));
//        assertThat(actual.getVolume(), is("2(2)"));
//        assertThat(actual.getPages(), is("33-36"));
//        assertThat(actual.getPmid(), is("1234567"));
    }


    /**
     * {@inheritDoc}
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestShowEditPublicationView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

}