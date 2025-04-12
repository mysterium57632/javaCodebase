package de.paull.std.files;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * Erstellt die Datei, wenn noch nicht vorhanden,
     * und alle Parent Dirs mit dazu
     * @param f File zum erstellen
     */
    public static boolean createFile(File f) {
        try {
            if (f.getParentFile() != null)
                f.getParentFile().mkdirs();
            f.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create File: " + f.getPath());
        }
        return false;
    }

    public static String readFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) createFile(f);
            return Reader.readRaw(f);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to get File content of File: " + filename);
            return "";
        }
    }
}
