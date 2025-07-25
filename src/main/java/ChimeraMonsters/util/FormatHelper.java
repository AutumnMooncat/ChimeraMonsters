package ChimeraMonsters.util;

public class FormatHelper {
    private static final StringBuilder newMsg = new StringBuilder();

    public static String capitalize(String str) {
        if (str.isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String capitalize(String str, String match) {
        return str.replace(match, capitalize(match));
    }

    public static String uncapitalize(String str) {
        if (str.isEmpty()) {
            return str;
        } else if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String uncapitalize(String str, String match) {
        return str.replace(match, uncapitalize(match));
    }

    public static String removeFormatting(String input) {
        return input.replaceAll("#.","");
    }

    public static String prefixWords(String input, String prefix) {
        newMsg.setLength(0);
        for (String word : input.split(" ")) {
            newMsg.append(prefix).append(word).append(' ');
        }

        return newMsg.toString().trim();
    }
}
