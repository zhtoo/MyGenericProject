package com.zht.genericproject.util.encryption.rsa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Description: RSA加密算法工具类
 */
public final class RSAUtil {
    // 算法类别
    private static final String ALGORITHM       = "RSA";
    // RSA密钥文件路径
    private static final String KEY_PATH        = "D:/RSAKey.txt";
    /************************************************************************
     * PEM证书
     ************************************************************************/
    ///////////////////////////////////////////////////////////////////////////
    // PEM证书文件路径
    ///////////////////////////////////////////////////////////////////////////
    // 公钥PEM证书文件路径
    private static final String PUB_PEM_PATH    = RSAUtil.class.getResource("/").getPath() + "public_key.pem";
    // 私钥PEM证书文件路径
    private static final String PRI_PEM_PATH    = RSAUtil.class.getResource("/").getPath() + "pkcs8_private_key.pem";
    ///////////////////////////////////////////////////////////////////////////
    // PEM证书字符串
    ///////////////////////////////////////////////////////////////////////////
    private static final String PUB_PEM         = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDYumivuqSmpUiLJg7jQQvwDlhhKNS4Hc69EymLlRySVVbgu0T8PJs+ZGCxKIYui2" +
            "/F7JPpi96mNgTxcP5kbbPnzboHwppChUEwEDg7a4gpMnr8quKHtkeGeqMnAe0KpHhxw4b6TDqfe+aHr0k7EGncs9c2O/nhDmOhq8pc2kXKqwIDAQAB";
    private static final String PRI_PEM         =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANi6aK+6pKalSIsmDuNBC/AOWGEo1Lgdzr0TKYuVHJJVVuC7RPw8mz5kYLEohi6Lb8Xsk+mL3qY2BPFw/mRts" +
                    "+fNugfCmkKFQTAQODtriCkyevyq4oe2R4Z6oycB7QqkeHHDhvpMOp975oevSTsQadyz1zY7+eEOY6GrylzaRcqrAgMBAAECgYBPfiADxKu7zAgNscnoJOWiCUD1ciqD1bsMssicP" +
                    "/cY3WBeUZH+CqTOf7bPhBOjYtV+fUXLddgWkt3BSTal028WvBl5R1Hypvb/WVm6Z2zzHuTh174GUjqMlLg6cZFtVyoKF" +
                    "/ZqSJlMEmLwqqcXsVbutWT5Puhki74ANxJuedR88QJBAO7RcE1Hk42hn9YkRzouqrhP4+VIUCiwl8QiM4JYyDYU796BWGKzPnZ0nX" +
                    "/1riWXG9DmrsibLmcTmvDyGZXDg6cCQQDoUh3UPgXsgjWM/GC5KautOTXnMXx0YgP" +
                    "/vpRwvBSAwWzfM6iGjdNcZoeSb56iSpsY9XFHbsN6k92yBRT7FDFdAkEAy0vUyqRssjts4hEd5SSGxcGUYcEuI4U4nIj0yjgjrw4hp4r6H4MwYAwjayNL7Mn2rt1A2ppnZO2mjwe4yemWrwJAfeSR2YPKvgFJd58D4xMbvwDzntcINCExPZNO6VBiY+ttTf62YkPUt4uwB8CI3DVFmt7WhfkV4QTj7CYo2m502QJBAKfxkNGjncqIM2aSrj+DqGZwAdW1ONVl34Th4rfjmwnq9Q4gard0M4rYL6tUr7iYj8PHuOS82xltYkUyyl/e3XI=";
    /************************************************************************
     * 模数、指数
     ************************************************************************/
    ///////////////////////////////////////////////////////////////////////////
    // 十进制 - 模数、指数
    ///////////////////////////////////////////////////////////////////////////
    // 模数
    private static final String MODULUS         =
            "122019541745047421152514651415209834828890211380881537184171652240704397449695172591406150033332314309438375604113215956944733818826669760966076573783148844126052976851653863744090531770348050285247220067444989655229461339004741326935263078065316727470569575434222769247071180037290501098163526145580771375441";
    // 公钥指数
    private static final String PUB_EXPONENT    = "65537";
    // 私钥指数
    private static final String PRI_EXPONENT    =
            "59093004492821766329854319258851411380352265422857303640514885796843262136745274926814468100583400580851497677631709740413337605867520202989186327956778010303991990110258503624620732329785151051503387408289462494887022380272571290260066671548213874960139508761320859026428318220531946164352808080903851659773";
    ///////////////////////////////////////////////////////////////////////////
    // 十六进制 - 模数、指数
    ///////////////////////////////////////////////////////////////////////////
    // 模数 - 16进制
    private static final String MODULUS_16      =
            "adc2f7e7dc87607c8e467da33ce16e76f836ca6f491eeeec699f47890fddf3a07328d657d2f7a2ee16127e44cfc4092a7394dcecfe60e0f7ce8def8f2d186ea312997e15cb6fd38e29cfc61a25fc8a705344306b3ab08efd518c62e2fa3ef6c531163859a41da4b3bed81a6687d52db4aea4f9ce7753a8bbec2f079f2cf0f951";
    // 公钥指数 - 16进制
    private static final String PUB_EXPONENT_16 = "10001";
    // 私钥指数 - 16进制
    private static final String PRI_EXPONENT_16 =
            "5426b71ec02b83ebee8f6eeb2e61d2b8a52f0bcce771a6ae31dce4ad19551956dc4eabb7aec12553feb6881655349067d1c0075a733b81d8215d95e61625ddf62937a4995eed9ef187a4fb2c103490054956cfc7e678852c06c3b1b88d3aca9e5bf33678b531b10631850b092506c4c2cbacc732322f40dfd535ff02e9dad1fd";

    /**
     * 测试用main方法
     */
    public static void main(String[] args) {
        try {
            /** 保存密钥文件 */
            // saveKeyPair(generateRSAKeyPair());
            //
            /** 文件中获取公钥和私钥 */
            // PublicKey publicKey = readKeyPair().getPublic();
            // PrivateKey privateKey = readKeyPair().getPrivate();

            /** PEM证书文件中读取字符串生成公钥和私钥 */
            // PublicKey publicKey = loadPublicKey(readFile(PUB_PEM_PATH));
            // PrivateKey privateKey = loadPrivateKey(readFile(PRI_PEM_PATH));

            /** PEM证书字符串生成公钥和私钥 */
            PublicKey publicKey = loadPublicKey(PUB_PEM);
            PrivateKey privateKey = loadPrivateKey(PRI_PEM);

            /** 十进制 MODULUS 和 EXPONENT 生成公钥和私钥 */
            // PublicKey publicKey = getPublicKey(MODULUS, PUB_EXPONENT);
            // PrivateKey privateKey = getPrivateKey(MODULUS, PRI_EXPONENT);

            /** 十六进制 MODULUS 和 EXPONENT 生成公钥和私钥 */
            // PublicKey publicKey = getPublicKey(MODULUS_16, PUB_EXPONENT_16);
            // PrivateKey privateKey = getPrivateKey(MODULUS_16, PRI_EXPONENT_16);

            // 打印密钥信息
            printPrivateKeyInfo(privateKey);
            printPublicKeyInfo(publicKey);

            /** 加解密时使用的KEY */
            // 加密时使用的key
            Key encryptKey = publicKey;
            // Key encryptKey = privateKey;

            // 解密时使用的key
            Key decryptKey = privateKey;
            // Key decryptKey = publicKey;

            System.out.println("-------------------- content --------------------");
            // 待加密字符串
            String data = "a1111111";
            System.out.println("待加密内容: " + data);

            System.out.println("-------------------- encrypt --------------------");
            // 对"待加密内容"进行RSA加密
            byte[] encrypt = encryptData(data.getBytes(), encryptKey);
            System.out.println("RSA加密后: " + new String(encrypt));
            // 对"RSA加密结果"进行Base64加密
            String base64Encrypt = Base64Util.encode(encrypt);
            System.out.println("Base64加密后: " + base64Encrypt);
            // 加"RSA加密结果"进行BCD加密
            String bcdEncrypt = BCDUtil.bcd2Str(encrypt);
            System.out.println("BCD加密后: " + bcdEncrypt);

            System.out.println("-------------------- decrypt --------------------");
            // 对"待解密内容"进行RSA解密
            byte[] decrypt = decryptData(encrypt, decryptKey);
            System.out.println("RSA解密后: " + new String(decrypt));
            // 对"RSA解密结果"进行Base64解密
            byte[] base64Decrypt = decryptData(Base64Util.decode(base64Encrypt), decryptKey);
            System.out.println("Base64解密后: " + new String(base64Decrypt));
            // 对"RSA解密结果"进行BCD解密
            byte[] bcdData = bcdEncrypt.getBytes();
            byte[] bcdDecrypt = decryptData(BCDUtil.ASCII_To_BCD(bcdData, bcdData.length), decryptKey);
            System.out.println("BCD解密后: " + new String(bcdDecrypt));

            System.out.println("-------------------- test --------------------");
            // 待解密字符串
            String dec = "OLSw6IuMtPnLdZHdykZmQ60VT1xwOz4g+uBipdi7MYZ4pPLfSP2HvD3Vf8xIk/tbhF73QdIjesjG7wVLs0gG7coGCprowTO3tWa2DzgDXpdvAhw8PJROAK" +
                    "+ze5zNmqtJAJi6mWlyR96aXnk94eOVTnPknEx/3ZhfiQ8+dCif4Ms=";
            byte[] decr = decryptData(Base64Util.decode(dec), privateKey);
            System.out.println("解密后: " + new String(decr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成RSA密钥对(默认密钥长度为1024)
     */
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength
     *         密钥长度，范围：512～2048
     *         一般1024
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
            kpg.initialize(keyLength);
            KeyPair kp = kpg.genKeyPair();
            printPublicKeyInfo(kp.getPublic());
            printPrivateKeyInfo(kp.getPrivate());
            return kp;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用Key加密
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data
     *         需加密数据的byte数据
     * @param key
     *         公钥
     *
     * @return 加密后的byte型数据
     */
    public static byte[] encryptData(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用Key解密
     *
     * @param encryptedData
     *         经过encryptedData()加密返回的byte数据
     * @param key
     *         私钥
     *
     * @return 解密后的byte型数据
     */
    public static byte[] decryptData(byte[] encryptedData, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过公钥byte[]将公钥还原，适用于RSA算法
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        PKCS8EncodedKeySpec keySpec    = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey  = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 通过私钥byte[]将私钥还原，适用于RSA算法
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        PKCS8EncodedKeySpec keySpec    = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 使用N、e值还原公钥
     * 通过模数+指数获得公钥
     *
     * @param modulus
     *         模数
     * @param publicExponent
     *         指数
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey getPublicKey(String modulus, String publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        RSAPublicKeySpec keySpec    = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
        RSAPublicKey publicKey  = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 使用N、d值还原私钥
     * 通过模数+指数获得私钥
     *
     * @param modulus
     *         模数
     * @param privateExponent
     *         指数
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String privateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        RSAPrivateKeySpec keySpec    = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *         公钥数据字符串
     *
     * @return 公钥
     *
     * @throws Exception
     *         加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Util.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     *         私钥数据字符串
     *
     * @return 私钥
     *
     * @throws Exception
     *         加载私钥时产生的异常
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Util.decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 从文件输入流中加载公钥
     *
     * @param in
     *         公钥输入流
     *
     * @return 公钥
     *
     * @throws Exception
     *         加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(InputStream in) throws Exception {
        try {
            return loadPublicKey(readKey(in));
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从文件输入流中加载私钥
     *
     * @param in
     *         私钥输入流
     *
     * @return 私钥
     *
     * @throws Exception
     *         加载公钥时产生的异常
     */
    public static PrivateKey loadPrivateKey(InputStream in) throws Exception {
        try {
            return loadPrivateKey(readKey(in));
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 读取密钥信息
     *
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String readLine;
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        return sb.toString();
    }

    /**
     * 保存密钥文件
     *
     * @throws Exception
     */
    public static void saveKeyPair(KeyPair kp) throws Exception {
        FileOutputStream fos = new FileOutputStream(KEY_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(kp);
        oos.close();
        fos.close();
    }

    /**
     * 读取密钥文件
     *
     * @throws Exception
     */
    public static KeyPair readKeyPair() throws Exception {
        FileInputStream fis = new FileInputStream(KEY_PATH);
        ObjectInputStream oos = new ObjectInputStream(fis);
        KeyPair kp  = (KeyPair) oos.readObject();
        oos.close();
        fis.close();
        return kp;
    }

    /**
     * 读取源文件内容
     *
     * @param path
     *         文件路径
     *
     * @return byte[] 文件内容
     *
     * @throws IOException
     */
    private static String readFile(String path) throws IOException {
        BufferedReader br  = new BufferedReader(new FileReader(path));
        String s   = br.readLine();
        String str = "";
        s = br.readLine();
        while (s.charAt(0) != '-') {
            // str += s + "\r";
            str += s;
            s = br.readLine();
        }
        br.close();
        return str;
    }

    /**
     * 打印公钥信息
     */
    public static void printPublicKeyInfo(PublicKey publicKey) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        System.out.println("----------RSAPublicKey----------");
        System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
        System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
        System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
        System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
        System.out.println("PublicEncoded=" + Base64Util.encode(rsaPublicKey.getEncoded()));
    }

    /**
     * 打印私钥信息
     */
    public static void printPrivateKeyInfo(PrivateKey privateKey) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        System.out.println("----------RSAPrivateKey ----------");
        System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
        System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
        System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
        System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());
        System.out.println("PrivatecEncoded=" + Base64Util.encode(rsaPrivateKey.getEncoded()));
    }
}
