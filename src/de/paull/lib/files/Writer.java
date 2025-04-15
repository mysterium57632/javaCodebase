package de.paull.lib.files;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

/**
 * Writer class to write to files
 */
public class Writer {

    /**
     *
     * @param f file to write to
     * @param content content which will be written
     * @param append true for append, false for overwrite
     * @throws IOException on error
     */
    public static void write(File f, String content, boolean append) throws IOException {
        try (FileWriter writer = new FileWriter(f, append)) {
            writer.write(content);
        }
    }

    /**
     * Will compress the content and write it to the file
     * @param content content which will be written
     * @param file file to write to
     */
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
