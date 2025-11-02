package cv.beriholic.beeyes.utils;

import cn.hutool.crypto.digest.BCrypt;

public class BcryptUtil {
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
