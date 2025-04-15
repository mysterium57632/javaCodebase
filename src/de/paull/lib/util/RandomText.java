package de.paull.lib.util;

import java.util.Random;

/**
 * Random String generator
 */
public class RandomText {

    /**
     * Returns a random generated String with letters and numbers
     * @param length length of the String
     * @return the random String
     */
    public static String generate(int length) {
        String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }

    /**
     * Returns a random generated String with letters
     * @param length length of the String
     * @return the random String
     */
    public static String generateTextOnly(int length) {
        String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }

    /**
     * Returns a random generated String with numbers
     * @param length length of the String
     * @return the random String
     */
    public static String generateNumbersOnly(int length) {
        String alphabet = "0123456789";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }
}
