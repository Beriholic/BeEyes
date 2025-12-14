package cv.beriholic.beeyes.utils;

import cn.hutool.core.lang.Pair;
import cn.hutool.crypto.digest.BCrypt;

import java.security.SecureRandom;

public class BcryptUtil {
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    public static Pair<String, String> getRandomHash(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder();

        passwordBuilder.append(upperCase.charAt(random.nextInt(upperCase.length())));
        passwordBuilder.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        passwordBuilder.append(numbers.charAt(random.nextInt(numbers.length())));
        passwordBuilder.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 4; i < length; i++) {
            passwordBuilder.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        String password = shuffleString(passwordBuilder.toString());

        return new Pair<>(password, encrypt(password));
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        SecureRandom random = new SecureRandom();

        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }

        return new String(characters);
    }
}
