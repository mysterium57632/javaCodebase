package de.paull.lib.util;

import de.paull.lib.output.ANSI;

/**
 * Class for String array formating
 */
public class Table {

    public static String ANSI_TABLE_COLOR = ANSI.BLUE;

    /**
     * Creates a formated Version of that list and returns it.
     * @param content the strings in a 2D array
     * @param header table header
     * @return the formatted table as String
     */
    public static String convert(String[][] content, String header) {
        if (content.length == 0) return header;
        int colum = content[0].length;
        for (int i = 0; i < colum-1; i++)
            content = fillArray(content, i);
        StringBuilder output = new StringBuilder(header);
        for (int i = 0; i < content.length; i++)
            output.append("\n").append(getLine(content, i, colum));
        return output.toString();
    }

    private static String getLine(String[][] content, int index, int columns) {
        String[] s = content[index];
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < columns-1; i++)
            line.append(s[i]).append(ANSI_TABLE_COLOR).append(" | ").append(ANSI.RESET);
        line.append(s[columns - 1]);
        return line.toString();
    }

    private static String[][] fillArray(String[][] raw, int index) {
        int length = getMaxLength(raw, index);
        for (int i = 0; i < raw.length; i++)
            raw[i][index] = fillString(raw[i][index], length);
        return raw;
    }

    private static String fillString(String content, int length) {
        StringBuilder contentBuilder = new StringBuilder(content);
        while(contentBuilder.length() < length)
            contentBuilder.append(" ");
        content = contentBuilder.toString();
        return content;
    }

    private static int getMaxLength(String[][] arier, int index) {
        int l = -1;
        for (String[] s : arier)
            if (s[index].length() > l)
                l = s[index].length();
        return l;
    }

}
