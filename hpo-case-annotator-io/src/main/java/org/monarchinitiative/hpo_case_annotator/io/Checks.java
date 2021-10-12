package org.monarchinitiative.hpo_case_annotator.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Checks {

    private Checks() {
    }

    /**
     * I don't want these characters in filenames
     */
    private static final Set<Character> ILLEGAL_CHARACTERS = new HashSet<>(Arrays.asList('/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|'));

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

    public static String makeLegalFileNameWithNoWhitespace(String name, char replacement) {
        char[] chars = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (ILLEGAL_CHARACTERS.contains(chars[i])) {
                chars[i] = replacement;
            }
        }
        return new String(chars).replaceAll("\\s+", String.valueOf(replacement));
    }

    public static String makeLegalFileNameWithNoWhitespace(String name) {
        return makeLegalFileNameWithNoWhitespace(name, '_');
    }
}
