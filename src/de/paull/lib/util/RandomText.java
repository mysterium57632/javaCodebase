package de.paull.lib.util;

import java.util.Random;

public class RandomText {

    public static String generate(int length) {
        String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }

    public static String generateTextOnly(int length) {
        String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }

    public static String generateNumbersOnly(int length) {
        String alphabet = "0123456789";
        Random r = new Random();
        String id = "";
        for (int i = 0; i < length; i++) id += alphabet.charAt(r.nextInt(alphabet.length())) + "";
        return id;
    }
}
