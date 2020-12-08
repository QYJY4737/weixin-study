package cn.congee.api.service;

import cn.congee.api.config.WeixinConfig;
import cn.congee.api.template.Data;
import cn.congee.api.vo.JsConfigModel;
import cn.congee.api.vo.JsConfigVo;
import cn.congee.api.vo.UploadImageModel;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: yang
 * @Date: 2020-12-09 6:53
 */
@Slf4j
@Service
public class WeixinService {

    @Autowired
    private WeixinConfig weixinConfig;

    /**
     * 微信公众号后台回调微信服务器校验信息
     *
     * @return
     */
    public String checkSignature() {
        return weixinConfig.checkSignature();
    }

    /**
     * 获取微信公众号的openId
     *
     * @param code
     * @param httpHeaders
     * @return
     */
    public String getOpenId(String code, HttpHeaders httpHeaders) {
        return weixinConfig.getOpenId(code, httpHeaders);
    }

    /**
     * 获取签名信息
     *
     * @param jsConfigModel
     * @return
     */
    public JsConfigVo getJsConfig(JsConfigModel jsConfigModel) {
        return weixinConfig.getJsConfig(jsConfigModel);
    }

    /**
     * 上传微信服务器图片到自身服务器
     *
     * @param uploadImageModel
     * @return
     */
    public String upload(UploadImageModel uploadImageModel) {
        return weixinConfig.upload(uploadImageModel);
    }

    /**
     * 定时发送微信推送
     *
     * @param openId      用户openId
     * @param templateId  模版id
     * @param url         详情路径
     * @param data        消息主体
     * @param miniProgram 小程序
     */
    public ReturnT<String> sendMessByTemplate(String openId, String templateId, String url, Data data, Map<String, String> miniProgram) {
        return weixinConfig.sendMessByTemplate(openId, templateId, url, data, miniProgram);
    }

}


