package com.shop.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author scorpio
 */
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String SALT = "1a2b3c4d";

    /**
     * 第一次MD5加密，用于网络传输
     *
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        // 避免在网络传输被截取然后反推出密码，所以在md5加密前先打乱密码
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 第二次MD5加密，用于存储到数据库
     *
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 合并
     *
     * @param input
     * @param saltDB
     * @return
     */
    public static String inputPassToDbPass(String input, String saltDB) {
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }
}
