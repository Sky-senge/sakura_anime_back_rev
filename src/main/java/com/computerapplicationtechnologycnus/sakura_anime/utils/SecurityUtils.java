package com.computerapplicationtechnologycnus.sakura_anime.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecurityUtils {
    public static String sha256Hash(String input) {
        try {
            // SHA-256存储密码
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 将输入字符串转换为字节
            byte[] inputBytes = input.getBytes("UTF-8");
            // 执行散列
            byte[] hashedBytes = digest.digest(inputBytes);
            // 将散列结果转换为Base64编码字符串
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // 验证输入字符串的散列是否与预期散列匹配
    public static boolean verifyHash(String input, String expectedHash) {
        String hashedInput = sha256Hash(input);
        return hashedInput.equals(expectedHash); // 比较散列结果
    }
}
