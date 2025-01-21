package xyz.beriholic.beeyes.utils;


import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Boolean check(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}