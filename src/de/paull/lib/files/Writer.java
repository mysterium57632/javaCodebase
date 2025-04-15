package de.paull.lib.files;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

public class Writer {

    /**
     * Schreibt in eine File einen gewissen content<br>
     * @param append <br>
     *               <b>true:</b> ob content hinten angehangen wird<br>
     *               <b>false:</b> ob die Datei alles mit content überschreibt
     */
    public static void write(File f, String content, boolean append) throws IOException {
        try (FileWriter writer = new FileWriter(f, append);) {
            writer.write(content);
        }
    }

    public static void writeCompressString(String content, File file) {
        FileUtil.createFile(file);
        try (DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(file));
             BufferedOutputStream bos = new BufferedOutputStream(dos);
             BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content.getBytes()))) {
            byte[] data = new byte[2048];
            while (bis.read(data) != -1)
                bos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
