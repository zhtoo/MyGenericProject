/*
 * DES
 * 概述
 * DES算法全称为Data Encryption Standard，即数据加密算法，它是IBM公司于1975年研究成功并公开发表的。
 * DES算法的入口参数有三个：Key、Data、Mode。
 *      其中Key为8个字节共64位，是DES算法的工作密钥；
 *      Data也为8个字节64位，是要被加密或被解密的数据；
 *      Mode为DES的工作方式,有两种：加密或解密。
 *
 * 算法原理
 * DES算法把64位的明文输入块变为64位的密文输出块，它所使用的密钥也是64位，其算法主要分为两步：
 * （1）初始置换
 *      其功能是把输入的64位数据块按位重新组合,并把输出分为L0、R0两部分，每部分各长32位，其置换规则为将输入的第58位换到第一位，第50位换到第2位……依此类推,
 *      最后一位是原来的第7位。L0、R0则是换位输出后的两部分，L0是输出的左32位，R0是右32位，
 *      例：设置换前的输入值为D1D2D3……D64，则经过初始置换后的结果为:L0=D58D50……D8；R0=D57D49……D7。
 * （2）逆置换
 *      经过16次迭代运算后，得到L16、R16,将此作为输入，进行逆置换，逆置换正好是初始置换的逆运算，由此即得到密文输出。
 */
package com.zht.genericproject.util.encryption.des;

import android.util.Base64;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Description: DES加密算法
 * 加密：DESUtil.encrypt(key, data)
 * 解密：DESUtil.decrypt(key, data)
 */
public class DESUtil {
    // 算法名称
    private static final String KEY_ALGORITHM    = "DES";
    // 算法名称/加密模式/填充方式
    // DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    // 常采用的是 NoPadding（不填充）、Zeros 填充（0填充）、PKCS5Padding 填充
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    private static final String IV_KEY           = "12345678";

    /**
     * 生成密钥key对象
     *
     * @param keyStr
     *         密钥,长度不能够小于8位字节
     *
     * @return 密钥对象
     *
     * @throws Exception
     */
    private static SecretKey keyGenerator(String keyStr) throws Exception {
        byte       input[]   = hexString2Bytes(keyStr);
        DESKeySpec secretKey = new DESKeySpec(input);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成SecretKeyFactory
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generateSecret(secretKey);
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
        Key secretKey = keyGenerator(key);
        // 实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // CBC模式,需要初始化向量IV
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV_KEY.getBytes());
        // 初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        // // ECB模式,不需要初始化向量IV
        // SecureRandom random = new SecureRandom();
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
        byte[] results = cipher.doFinal(data.getBytes());
        //  // 该部分是为了与加解密在线测试网站（http://tripledes.online-domain-tools.com/）的十六进制结果进行核对
        //  for (int i = 0; i < results.length; i++) {
        //      System.out.print(results[i] + " ");
        //  }
        //  System.out.println();
        // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
        // java版本
        // return Base64.encodeBase64String(results);
        // android版本
        return Base64.encodeToString(results, Base64.DEFAULT);
    }

    /**
     * 解密数据
     *
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
        Key secretKey = keyGenerator(key);
        // 实例化Cipher对象，它用于完成实际的解密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // CBC模式,需要初始化向量IV
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV_KEY.getBytes());
        // 初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        // // ECB模式,不需要初始化向量IV
        // cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 执行解密操作
        // java版本
        // return new String(cipher.doFinal(Base64.decodeBase64(data)));
        // android版本
        return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
    }

    public static void main(String[] args) throws Exception {
        String key  = "A1B2C3D4E5F60708";
        String data = "I believe you can.";
        System.out.println("原  文: " + data);
        String encryptData = encrypt(key, data);
        System.out.println("加密后: " + encryptData);
        String decryptData = decrypt(key, encryptData);
        System.out.println("解密后: " + decryptData);
    }
}
