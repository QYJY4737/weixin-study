package cn.congee.api.controller;

import cn.congee.api.common.JsonResult;
import cn.congee.api.service.WeixinService;
import cn.congee.api.template.Data;
import cn.congee.api.vo.JsConfigModel;
import cn.congee.api.vo.JsConfigVo;
import cn.congee.api.vo.UploadImageModel;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: yang
 * @Date: 2020-12-09 6:58
 */
@RestController
@RequestMapping(value = "/weixi")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @GetMapping(value = "/unauth", name = "微信公众号后台回调微信服务器校验信息")
    public JsonResult checkSignature() {
        return new JsonResult(weixinService.checkSignature());
    }

    @GetMapping(value = "/unauth/openid", name = "获取微信公众号的openId")
    public JsonResult getOpenId(String code, HttpHeaders httpHeaders) {
        return new JsonResult(weixinService.getOpenId(code, httpHeaders));
    }

    @PostMapping(value = "/unauth/jsconfig", name = "获取签名信息")
    public JsonResult<JsConfigVo> getJsConfig(@RequestBody JsConfigModel jsConfigModel) {
        return new JsonResult<>(weixinService.getJsConfig(jsConfigModel));
    }

    @PostMapping(value = "/unauth/image/upload", name = "上传微信服务器图片到自身服务器")
    public JsonResult upload(@RequestBody UploadImageModel uploadImageModel) {
        return new JsonResult(weixinService.upload(uploadImageModel));
    }

    @PostMapping(value = "/unauth/sendMessByTemplate", name = "发送微信推送")
    public JsonResult<ReturnT<String>> sendMessByTemplate(@RequestParam(value = "openId") String openId,
                                                          @RequestParam(value = "templateId") String templateId,
                                                          @RequestParam(value = "url") String url,
                                                          @RequestBody Data data,
                                                          @RequestBody Map<String, String> miniProgram) {
        return new JsonResult<>(weixinService.sendMessByTemplate(openId, templateId, url, data, miniProgram));
    }


}
