package org.monarchinitiative.hpo_case_annotator.core.io;

import java.io.File;

public class Checks {

    /** I don't want these characters in filenames */
    private static char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};


    public static boolean isLegalFileName(String fileName) {
        if (fileName == null) {
            return false;
        }
        for (Character c : ILLEGAL_CHARACTERS) {
            if (fileName.indexOf(c) > 0) {
                return false;
            }
        }

        /* Remove all whitespace characters.
         * http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java#5455809 */
        String noWhitespace = fileName.replaceAll("\\s+", "");
        return !noWhitespace.equals("");
    }


    public static boolean isLegalFileName(File file) {
        return isLegalFileName(file.getAbsolutePath());
    }
}
