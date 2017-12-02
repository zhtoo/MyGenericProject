/*
 * 3DES
 * 概述
 * 3DES（或称为Triple DES）是三重数据加密算法（TDEA，Triple Data Encryption Algorithm）块密码的通称。
 * 它相当于是对每个数据块应用三次DES加密算法。由于计算机运算能力的增强，原版DES密码的密钥长度变得容易被暴力破解；
 * 3DES即是设计用来提供一种相对简单的方法，即通过增加DES的密钥长度来避免类似的攻击，而不是设计一种全新的块密码算法。
 *
 * 算法原理
 * 使用3条56位的密钥对 数据进行三次加密。3DES（即Triple DES）是DES向AES过渡的加密算法（1999年，NIST将3-DES指定为过渡的加密标准）。
 * 其具体实现如下：设Ek()和Dk()代表DES算法的加密和解密过程，K代表DES算法使用的密钥，P代表明文，C代表密文，这样：
 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P)))
 * 3DES解密过程为：P=Dk1(EK2(Dk3(C)))
 *
 */
package com.zht.genericproject.util.encryption.des;

import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Description: DES加密算法
 * 加密：ThreeDESUtil.encrypt(key, data)
 * 解密：ThreeDESUtil.decrypt(key, data)
 */
public class ThreeDESUtil {
    // 算法名称
    private static final String KEY_ALGORITHM    = "desede";
    // 算法名称/加密模式/填充方式
    // DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    // 常采用的是 NoPadding（不填充）、Zeros 填充（0填充）、PKCS5Padding 填充
    private static final String CIPHER_ALGORITHM = "desede/CBC/PKCS5Padding";
    private static final String IV_KEY           = "12345678";

    /**
     * 生成密钥key对象
     *
     * @param keyStr
     *         密钥,长度不能够小于24位字节
     *
     * @return 密钥对象
     *
     * @throws Exception
     */
    private static SecretKey keyGenerator(String keyStr) throws Exception {
        byte          input[]   = hexString2Bytes(keyStr);
        DESedeKeySpec secretKey = new DESedeKeySpec(input);
        // 创建一个密匙工厂，然后用它把DESedeKeySpec转换成SecretKeyFactory
        SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return KeyFactory.generateSecret(secretKey);
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] hexString2Bytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int    j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /**
     * 加密数据
     *
     * @param key
     *         密钥
     * @param data
     *         待加密数据 - 明文
     *
     * @return Base64编码的密文
     *
     * @throws Exception
     */
    public static String encrypt(String key, String data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Key secretKey = keyGenerator(key);
        // 实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // CBC模式,初始化向量IV
        IvParameterSpec paramSpec = new IvParameterSpec(IV_KEY.getBytes());
        // 初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        byte[] results = cipher.doFinal(data.getBytes());
        // // 该部分是为了与加解密在线测试网站（http://tripledes.online-domain-tools.com/）的十六进制结果进行核对
        // for (int k = 0; k < out.length; k++) {
        //     System.out.print(out[k] + " ");
        // }
        // System.out.println("");
        // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
        // java版本
        // return Base64.encodeBase64String(results);
        // android版本
        return Base64.encodeToString(results, Base64.DEFAULT);
    }

    /**
     * @param key
     *         密钥
     * @param data
     *         Base64编码的密文
     *
     * @return 解密后的数据 - 明文
     *
     * @throws Exception
     */
    public static String decrypt(String key, String data) throws Exception {
        Key desKey = keyGenerator(key);
        // 实例化Cipher对象，它用于完成实际的解密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // CBC模式,需要初始化向量IV
        IvParameterSpec paramSpec = new IvParameterSpec(IV_KEY.getBytes());
        // 初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, desKey, paramSpec);
        // 执行解密操作
        // java版本
        // return new String(cipher.doFinal(Base64.decodeBase64(data)));
        // android版本
        return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
    }

    public static void main(String[] args) throws Exception {
        String key  = "6C4E60E55552386C759569836DC0F83869836DC0F838C0F7";
        String data = "I believe you can.";
        System.out.println("CBC加密解密");
        System.out.println("原  文：" + data);
        String encryptData = encrypt(key, data);
        System.out.println("加密后: " + encryptData);
        String decryptData = decrypt(key, encryptData);
        System.out.println("解密后: " + decryptData);
    }
}
