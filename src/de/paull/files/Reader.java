package de.paull.std.files;

import java.io.*;
import java.util.zip.InflaterInputStream;

/**
 * Er ist schon groß, er kann schon lesen ;) <b>uWu</b>
 */
public class Reader {

    /**
     * Liest inhalt einer File als ganzen <b>String</b> ein und gibt zurück <br>
     * Bei fehlern -> <b>IOException</b>
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
     * Ließt Datei als InputStream
     * @param path path der Datei
     * @return InputStream
     * @throws IOException bei fehlern
     */
    public static InputStream readToStream(String path) throws IOException {
        File f = new File(path);
        return new FileInputStream(f);
    }

    /**
     * Liest inhalt einer File als <b>String</b> aus dem <b>Resosurces Folder</b> ein und gibt zurück <br>
     * Bei fehlern -> <b>IOException</b>
     */
    public static InputStream readResourceToInputStream(String path) throws IOException {
        if (path.startsWith("/")) path = path.substring(1);
        InputStream f = Reader.class.getClassLoader().getResourceAsStream(path);
        return f;
    }

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
