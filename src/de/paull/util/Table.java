package de.paull.std.util;

import de.paull.std.output.ANSI;

public class Table {

    public static String convert(String[][] content, String header) {
        if (content.length == 0) return header;
        int colum = content[0].length;
        for (int i = 0; i < colum-1; i++)
            content = fillArray(content, i);
        String output = header;
        for (int i = 0; i < content.length; i++)
            output += "\n" + getLine(content, i, colum);
        return output;
    }

    private static String getLine(String[][] content, int index, int colums) {
        String[] s = content[index];
        String line = "";
        for (int i = 0; i < colums-1; i++)
            line += s[i] + ANSI.BLUE + " | " + ANSI.RESET;
        line += s[colums-1];
        return line;
    }

    private static String[][] fillArray(String[][] raw, int index) {
        int length = getMaxLength(raw, index);
        for (int i = 0; i < raw.length; i++)
            raw[i][index] = fillString(raw[i][index], length);
        return raw;
    }

    private static String fillString(String content, int length) {
        while(content.length() < length)
            content += " ";
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
