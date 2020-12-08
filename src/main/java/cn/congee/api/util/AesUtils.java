package cn.congee.api.util;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.entity.ApiKeyContent;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Base64;
/**
 * @Author: yang
 * @Date: 2020-12-09 3:46
 */
@Slf4j
public class AesUtils {

    static {
        //如果是PKCS7Padding填充方式，则必须加上下面这行
        Security.addProvider(new BouncyCastleProvider());
    }

    static String mode = "AES/CBC/PKCS7Padding";

    /**
     * 加密
     *
     * @param dataByte
     * @param keyByte
     * @param ivByte
     * @return
     * @throws Exception
     */
    public static String encrypt(byte[] dataByte, byte[] keyByte, byte[] ivByte) throws Exception {
        Long now = System.currentTimeMillis();
        //加密之前，先从Base64格式还原到原始格式
        Base64.Decoder decoder = Base64.getDecoder();

        String encryptedData = null;

        //指定算法，模式，填充方式，创建一个Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        //生成Key对象
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");

        //把向量初始化到算法参数
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));

        //指定用途，密钥，参数 初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, params);

        //指定加密
        byte[] result = cipher.doFinal(dataByte);

        //对结果进行Base64编码，否则会得到一串乱码，不便于后续操作
        Base64.Encoder encoder = Base64.getEncoder();
        encryptedData = encoder.encodeToString(result);
        log.info("加密耗时[{}]ms", System.currentTimeMillis() - now);

        return encryptedData;
    }

    /**
     * 解密
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String sessionKey, String iv) throws Exception {
        Long now = System.currentTimeMillis();
        //解密之前先把Base64格式的数据转成原始格式
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(encryptedData);
        byte[] keyByte = sessionKey.getBytes();
        byte[] ivByte = iv.getBytes();

        String data = null;

        //指定算法，模式，填充方法 创建一个Cipher实例
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        //生成Key对象
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");

        //把向量初始化到算法参数
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));

        //指定用途，密钥，参数 初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);

        //执行解密
        byte[] result = cipher.doFinal(dataByte);

        //解密后转成字符串
        data = new String(result, GlobalConstants.CHARSET);

        log.info("解密耗时[{}]ms", System.currentTimeMillis() - now);
        return data;
    }

    public static void main(String[] args) throws Exception {
        //加密
        ApiKeyContent apiKey = new ApiKeyContent(ApiKeyCntUtils.APP_ENTRY_SWIFT,
                ApiKeyCntUtils.API_KEY_DEFAULT,
                System.currentTimeMillis());
        String enc = encrypt(JSON.toJSONString(apiKey).getBytes(), GlobalConstants.AES_PWD.getBytes(), GlobalConstants.AES_IV.getBytes());
        System.out.println(enc);
        String desc = AesUtils.decrypt("gc8U4S37ZhhoQZNeZZ0CfawpVKnxUmFHJ3hg11qquxjEZOXozvY9cAVyhK7VoCSylEzOn0tt4YNrD4UQ3x2USLNpqsNVBzF2n8SQw0LTNxpP8OWMMV35H5nbeT/ngZT538YUFAZLpbLtMQvPpL+4ag==", GlobalConstants.AES_PWD, GlobalConstants.AES_IV);
        System.out.println(desc);

    }

}
