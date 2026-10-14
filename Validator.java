/**
 * Validator - a utility class with static helper methods that check user input.
 * Demonstrates: static methods, a private constructor (so the class cannot be
 * instantiated, as in Lecture 6), and the while, do-while and for loops.
 */
public class Validator {

    /** Private constructor: this class only contains static methods, so no objects are needed. */
    private Validator() {
    }

    /**
     * Cleans text typed by the user: removes spaces at both ends and replaces
     * repeated spaces with a single space. A do-while loop is used because the
     * replacement must run at least once.
     */
    public static String cleanText(String text) {
        if (text == null) {
            return "";
        }
        String result = text.trim();
        do {
            result = result.replace("  ", " ");
        } while (result.contains("  "));
        return result;
    }

    /** True if the text contains only the digits 0-9 and has at most maxDigits characters. */
    public static boolean isWholeNumber(String text, int maxDigits) {
        if (text == null || text.length() == 0 || text.length() > maxDigits) {
            return false;
        }
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            i++;
        }
        return true;
    }

    /** True if the text is a positive decimal such as 75 or 75.5 (digits and at most one dot). */
    public static boolean isDecimalNumber(String text) {
        if (text == null || text.length() == 0 || text.length() > 6) {
            return false;
        }
        int dots = 0;
        int digits = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '.') {
                dots++;
            } else if (c >= '0' && c <= '9') {
                digits++;
            } else {
                return false;
            }
        }
        return dots <= 1 && digits >= 1;
    }

    /** A valid name contains letters, spaces, dots, hyphens or apostrophes, and at least one letter. */
    public static boolean isValidName(String text) {
        if (text == null || text.length() == 0) {
            return false;
        }
        boolean hasLetter = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (c != ' ' && c != '.' && c != '-' && c != '\'') {
                return false;
            }
        }
        return hasLetter;
    }

    /** True if min <= value <= max. */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
