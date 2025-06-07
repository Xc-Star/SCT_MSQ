package com.sct.utils;


import org.mindrot.jbcrypt.BCrypt;

/**
 * @Title: PasswordUtil
 * @Author Xc_Star
 * @Package com.sct.utils
 * @Date 2025/6/8 01:24
 */
public class PasswordUtil {

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(CharSequence rawPassword) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }

    /**
     * 生成BCrypt加密的密码
     * @param rawPassword 原始密码
     * @param strength 加密强度(4-31)，默认10
     * @return 加密后的密码
     */
    public static String encode(CharSequence rawPassword, int strength) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(strength));
    }
}
