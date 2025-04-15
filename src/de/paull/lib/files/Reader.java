package de.paull.lib.files;

import java.io.*;
import java.util.zip.InflaterInputStream;

/**
 * Reader class to read Files
 */
public class Reader {

    /**
     * Read contents of a String to a String
     * @param f the file which should be read
     * @return returns the String
     */
    public static String readRaw(File f) throws IOException {
        try (FileReader reader = new FileReader(f)) {
            String content = "";
            int data;
            do {
                data = reader.read();
                content += (char) data;
            } while (data != -1);
            reader.close();
            if (content.endsWith("\uFFFF")) // Wierdes Zeichen am Ende entfernen
                content = content.substring(0, content.length()-1);
            return content;
        }
    }

    /**
     * Reads a file as InputStream
     * @param path path of file
     * @return InputStream
     * @throws IOException on error
     */
    public static InputStream readToStream(String path) throws IOException {
        File f = new File(path);
        return new FileInputStream(f);
    }

    /**
     * Reads a file out of the resources folder to InputStream
     * @param path path of file
     * @return InputStream
     * @throws IOException on error
     */
    public static InputStream readResourceToInputStream(String path) throws IOException {
        if (path.startsWith("/")) path = path.substring(1);
        InputStream f = Reader.class.getClassLoader().getResourceAsStream(path);
        return f;
    }

    /**
     * Reads a from the Writer compressed File
     * @param f path of file
     * @return raw uncompressed String
     * @throws IOException on error
     */
    public static String readCompressedFile(File f) throws IOException {
        if (!f.exists()) return "";
        if (f.length() == 0) return "";
        try (InflaterInputStream iis = new InflaterInputStream(new FileInputStream(f));
                BufferedInputStream bis = new BufferedInputStream(iis)) {
            StringBuilder builder = new StringBuilder();
            byte[] data = new byte[2048];
            while (bis.read(data) != -1)
                for (byte b : data)
                    builder.append((char) b);
            return builder.toString().trim();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
