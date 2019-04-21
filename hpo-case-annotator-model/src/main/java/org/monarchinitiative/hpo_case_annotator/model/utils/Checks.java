package org.monarchinitiative.hpo_case_annotator.model.utils;

import java.util.Arrays;
import java.util.List;

public class Checks {

    /** I don't want these characters in filenames */
    private static List<Character> ILLEGAL_CHARACTERS = Arrays.asList('/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':');

    public static boolean isLegalFileName(String fileName) {
        if (fileName == null) {
            return false;
        }
        char[] chars = fileName.toCharArray();
        for (int i = 0; i < fileName.length(); i++) {
            if (ILLEGAL_CHARACTERS.contains(chars[i])) {
                return false;
            }
        }

        /* Remove all whitespace characters.
         * http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java#5455809 */
        String noWhitespace = fileName.replaceAll("\\s+", "");
        return !noWhitespace.equals("");
    }


    /**
     * @param name
     * @param replacement
     * @return
     */
    public static String makeLegalFileNameWithNoWhitespace(String name, char replacement) {
        char[] chars = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (ILLEGAL_CHARACTERS.contains(chars[i])) {
                chars[i] = replacement;
            }
        }
        return new String(chars).replaceAll("\\s+", String.valueOf(replacement));
    }

    /**
     * @param name
     * @return
     */
    public static String makeLegalFileNameWithNoWhitespace(String name) {
        return makeLegalFileNameWithNoWhitespace(name, '_');
    }
}
