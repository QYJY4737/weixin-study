package cn.congee.api.util;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.exception.BaseExceptionMsg;
import cn.congee.api.exception.ServiceException;
import cn.congee.api.vo.IdCardCerVo;
import cn.hutool.core.util.IdcardUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.security.CertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yang
 * @Date: 2020-12-12 10:16
 */
@Slf4j
public class PublicSecurity {

    private static final String DEF_CHARSET = "UTF-8";
    private static final int DEF_CONN_TIMEOUT = 30000;
    private static final int DEF_READ_TIMEOUT = 30000;
    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    /**
     * 公安系统校验姓名 身份证
     *
     * @param name
     * @param idCard
     */
    public static void systemVerify(String name, String idCard){
        log.info("==========>开始调用公安系统校验身份证接口,姓名:{},身份证号:{}", name, idCard);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(idCard)) {
            log.error("==========>姓名或身份证参数不能为空!");
            throw new ServiceException(BaseExceptionMsg.IDENNO_CHECK_PARAM_ERROR);
        }
        if (!IdcardUtil.isValidCard(idCard)) {
            log.warn("身份证格式有误!,身份证号信息:{},姓名:{}", idCard, name);
            throw new ServiceException(BaseExceptionMsg.FAMILY_CONTACT_IDENNO_NOT_USE);
        }
        String result = null;
        Map<String, String> param = new HashMap(3);
        param.put("idcard", idCard);
        param.put("realname", name);
        param.put("key", GlobalConstants.CER_APP_KEY);
        try {
            result = net(GlobalConstants.CER_URL, param, "GET");
            log.info("==========>开始调用公安系统校验身份证接口返回结果:{}", result);
            //验证返回数据
            validataResponIdcard(result);
            IdCardCerVo idCardCerResult = readJson(toJson(readMap(result).get("result")),
                    IdCardCerVo.class);
            if (idCardCerResult == null || idCardCerResult.getRes() == 2) {
                throw new ServiceException(BaseExceptionMsg.IDENNO_CHECK_ERROR);
            }
            log.info("==========>调用公安系统校验身份证接口结束!返回结果:{}", result);
        } catch (Exception e) {
            log.error("==========>调用公安系统校验身份证接口,校验失败!失败信息:{}", e.getMessage());
            throw new ServiceException(e);
        }
    }

    public static void validataResponIdcard(String result) {
        if (result == null || readMap(result).get("error_code") == null) {
            throw new ServiceException(BaseExceptionMsg.IDCARD_OR_NAME_NOT_EXISTS);
        }
        switch (Integer.valueOf(readMap(result).get("error_code").toString())) {
            case 210304:
                throw new ServiceException(BaseExceptionMsg.FREQUENT_CERTIFICATION);
            case 210301:
                throw new ServiceException(BaseExceptionMsg.IDCARD_OR_NAME_NOT_EXISTS);
            default:
                break;
        }
    }


    /**
     * 将字符串转换为对象
     *
     * @param content
     * @param cls
     * @return
     */
    public static <T extends Object> T readJson(String content, Class<T> cls) {
        try {
            return mapper.readValue(content, cls);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("转换失败");
        }
    }

    /**
     * 将obj转换为json字符串
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String toJson(Object obj) {
        try {
            Assert.notNull(obj, "obj can not be null");
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json转为map
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static Map<String, Object> readMap(String content) {
        try {
            return mapper.readValue(content, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 认证接口
     *
     * @param params 请求参数
     * @param method 请求方法
     * @return 网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params, String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (method == null || "GET".equals(method)) {
                strUrl = strUrl + "?" + urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || "GET".equals(method)) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && "POST".equals(method)) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHARSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    /**
     * 将map型转为请求参数型
     *
     * @param data
     * @return
     */
    public static String urlencode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", DEF_CHARSET));
                if (index != data.size() - 1) {
                    sb.append("&");
                }
                index++;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
