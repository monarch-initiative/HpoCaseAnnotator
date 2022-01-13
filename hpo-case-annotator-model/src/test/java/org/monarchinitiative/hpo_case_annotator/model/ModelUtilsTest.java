package org.monarchinitiative.hpo_case_annotator.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelUtilsTest {

    private static final List<Character> ILLEGAL_CHARS = Arrays.asList('/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|');


    @Test
    public void isLegalFileName() {
        String legal = "file.json";
        assertThat(ModelUtils.isLegalFileName(legal), is(true));
    }

    @Test
    public void isIllegalFileName() {
        for (Character c : ILLEGAL_CHARS) {
            String illegal = "file" + c + "name.json";
            assertThat(ModelUtils.isLegalFileName(illegal), is(false));
        }
    }

    @Test
    public void nullIsIllegalFileName() {
        assertThat(ModelUtils.isLegalFileName(null), is(false));
    }

    @Test
    public void replaceIllegalCharactersInFileName() {
        for (Character c : ILLEGAL_CHARS) {
            String illegal = "file" + c + "name.json";
            assertThat(ModelUtils.makeLegalFileNameWithNoWhitespace(illegal, '-'), is("file-name.json"));
        }
    }

    @Test
    public void replaceIllegalCharactersInFileNameDoesNothingWithGoodInput() {
        assertThat(ModelUtils.makeLegalFileNameWithNoWhitespace("good_name.txt"), is("good_name.txt"));
    }

    @Test
    public void whitespaceIsReplaced() {
        assertThat(ModelUtils.makeLegalFileNameWithNoWhitespace("good name.txt"), is("good_name.txt"));
    }

    @Test
    public void replaceIllegalCharactersInFileNameWithDefault() {
        for (Character c : ILLEGAL_CHARS) {
            String illegal = "file" + c + "name.json";
            assertThat(ModelUtils.makeLegalFileNameWithNoWhitespace(illegal), is("file_name.json"));
        }
    }

}
