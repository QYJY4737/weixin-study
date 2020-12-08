package cn.congee.api.job;

import cn.congee.api.config.WeixinProperties;
import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.helper.RedisHelper;
import cn.congee.api.helper.RestTemplateHelper;
import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yang
 * @Date: 2020-12-09 3:23
 */
@Slf4j
@Component
@JobHandler(value = "weixinTokenScheduleJobHandler")
public class WeixinTokenScheduleJobHandler extends IJobHandler {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private WeixinProperties weixinProperties;
    @Autowired
    private RestTemplateHelper restTemplateHelper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            Map<String, Object> param = new HashMap<>(4);
            param.put("grant_type", weixinProperties.getGrant_type());
            param.put("appid", weixinProperties.getAppid());
            param.put("secret", weixinProperties.getSecret());
            String result = restTemplateHelper.get(GlobalConstants.ACCESS_TOKEN_URL, String.class, param, new HashMap<>());
            log.info("请求微信官方获取access_token返回的结果为:[{}]", JSON.toJSONString(result));
            Map map = JSON.parseObject(result, Map.class);
            String accessToken = map.get("access_token").toString();
            log.info("获取到的access_token值为:[{}]", accessToken);
            redisHelper.setObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, accessToken, GlobalConstants.ACCESS_TOKEN_EXPERI_SECONDS);
        } catch (Exception e) {
            log.error("获取access_token失败!异常信息:[{}]", e.getMessage());
            e.printStackTrace();
        }
        return ReturnT.SUCCESS;
    }

}
