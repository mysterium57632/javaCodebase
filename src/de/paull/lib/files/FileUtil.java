package de.paull.lib.files;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * Creates a file or dir, if not already present.
     * @param filename the file which will be created
     * @return true, if the creation was succesfull, false if there was an error
     */
    public static boolean createFile(File filename) {
        try {
            if (filename.getParentFile() != null)
                filename.getParentFile().mkdirs();
            filename.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create File: " + filename.getPath());
        }
        return false;
    }

    /**
     * Reads a file if present. Otherwise, creates the file
     * @param filename the file which will be created
     * @return content of the file or empty string when freshly created.
     *      Will return null if an error  occurs.
     */
    public static String readFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) createFile(f);
            return Reader.readRaw(f);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to read File content of: " + filename);
            return null;
        }
    }
}
