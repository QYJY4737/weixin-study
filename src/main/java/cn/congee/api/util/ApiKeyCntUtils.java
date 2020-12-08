package cn.congee.api.util;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.entity.ApiKeyContent;
import cn.congee.api.exception.ApiException;
import cn.congee.api.exception.BaseExceptionMsg;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: yang
 * @Date: 2020-12-09 3:42
 */
public class ApiKeyCntUtils {



    /**
     * 云上云下访问构造默认的apiKey
     *
     * @return
     */
    public static String buildDefaultApikeyCnt() {
        ApiKeyContent apiKey = new ApiKeyContent(GlobalConstants.APP_ENTRY_SWIFT,
                GlobalConstants.API_KEY_DEFAULT,
                System.currentTimeMillis());
        try {
            return AesUtils.encrypt(JSON.toJSONString(apiKey).getBytes(), GlobalConstants.AES_PWD.getBytes(), GlobalConstants.AES_IV.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(BaseExceptionMsg.ENC_API_FAILED);
        }
    }


    /**
     * 云上|云下访问 添加默认的headers
     *
     * @param headers
     * @return
     */
    public static HttpHeaders addDefaultHeaders(HttpHeaders headers) {
        List<String> key = headers.get(GlobalConstants.HTTP_HEADER_APIKEY);
        if (CollectionUtils.isEmpty(key)) {
            //写入默认的apiKey
            headers.set(GlobalConstants.HTTP_HEADER_APIKEY, ApiKeyCntUtils.buildDefaultApikeyCnt());
        }

        key = headers.get(GlobalConstants.HTTP_HEADER_APP_VERSION);
        if (CollectionUtils.isEmpty(key)) {
            //写入默认的apiKey
            headers.set(GlobalConstants.HTTP_HEADER_APP_VERSION, GlobalConstants.DEFAULT_APP_VERSION);
        }
        return headers;
    }

}
