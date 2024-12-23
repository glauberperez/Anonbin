package org.anonbin.utils;
import java.util.Random;

public class RandomSlugGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int length) {
        StringBuilder slug = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            slug.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return slug.toString();
    }
}

