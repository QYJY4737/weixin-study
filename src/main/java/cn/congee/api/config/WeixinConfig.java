package cn.congee.api.config;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.exception.BaseExceptionMsg;
import cn.congee.api.exception.ServiceException;
import cn.congee.api.helper.RedisHelper;
import cn.congee.api.helper.RestTemplateHelper;
import cn.congee.api.job.WeixinTokenScheduleJobHandler;
import cn.congee.api.template.Data;
import cn.congee.api.template.TempResult;
import cn.congee.api.template.TemplateBean;
import cn.congee.api.util.SHA1;
import cn.congee.api.util.Validates;
import cn.congee.api.vo.JsConfigModel;
import cn.congee.api.vo.JsConfigVo;
import cn.congee.api.vo.UploadImageModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @Author: yang
 * @Date: 2020-12-09 4:26
 */
@Slf4j
public class WeixinConfig {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private WeixinProperties weixinProperties;
    @Autowired
    private RestTemplateHelper restTemplateHelper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private WeixinTokenScheduleJobHandler weixinTokenScheduleJobHandler;

    /**
     * 微信公众号后台回调微信服务器校验信息
     *
     * @return
     */
    public String checkSignature(){
        //获取微信加密签名
        String signature = httpServletRequest.getParameter("signature");
        //获取时间戳
        String timestamp = httpServletRequest.getParameter("timestamp");
        //获取随机数
        String nonce = httpServletRequest.getParameter("nonce");
        //获取随机字符串
        String echostr = httpServletRequest.getParameter("echostr");
        //获取自定义token
        String token = weixinProperties.getToken();
        List<String> params = new ArrayList<String>(10);
        params.add(token);
        params.add(timestamp);
        params.add(nonce);
        //将token、timestamp、nonce三个参数进行字典序排序
        Collections.sort(params, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        String encryString = SHA1.encode(params.get(0) + params.get(1) + params.get(2));
        //校验
        if (StringUtils.equals(encryString, signature)) {
            log.info("微信公众号后台回调微信服务器校验信息成功:echostr=[{}]", echostr);
            return echostr;
        }
        log.info("微信公众号后台回调微信服务器校验信息失败!");
        return null;
    }

    /**
     * 获取微信公众号的openId
     *
     * @param code
     * @param httpHeaders
     * @return
     */
    public String getOpenId(String code, HttpHeaders httpHeaders){
        log.info("开始调用获取openid方法,传来参数code:{},headers={}", code , JSON.toJSONString(httpHeaders));
        Map<String, Object> paramMap = new HashMap<>(5);
        paramMap.put("appid", weixinProperties.getAppid());
        paramMap.put("code", code);
        paramMap.put("secret", weixinProperties.getSecret());
        paramMap.put("grant_type", "authorization_code");
        String openidResult = restTemplateHelper.get(GlobalConstants.WX_OPENID_URL, String.class, paramMap, new HashMap<>());
        log.info("访问微信获取openid接口返回结果为:openidResult=[{}]", openidResult);
        JSONObject jsonObject = JSON.parseObject(openidResult);
        String openId = jsonObject.get("openid").toString();
        log.info("获取到的openId为:[{}]", openId);
        return openId;
    }

    /**
     * 获取签名信息
     *
     * @param jsConfigModel
     * @return
     */
    public JsConfigVo getJsConfig(JsConfigModel jsConfigModel){
        log.info("开始执行获取签名信息接口入参为jsConfigModel=[{}]", JSON.toJSONString(jsConfigModel));
        String url = jsConfigModel.getUrl();
        String access_token = redisHelper.getObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, String.class);
        if (null == access_token) {
            try {
                weixinTokenScheduleJobHandler.execute("");
            } catch (Exception e) {
                e.printStackTrace();
            }
            access_token = redisHelper.getObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, String.class);
        }
        String ticketUrl = GlobalConstants.WX_TICKET_URL;
        Map<String, Object> paramMap = new HashMap<>(10);
        paramMap.put("access_token", access_token);
        paramMap.put("type", "jsapi");
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Column", "application/x-www-form-urlencoded");
        String ticket = null;
        try {
            String ticketResult = restTemplateHelper.get(ticketUrl, String.class, paramMap, headerMap);
            JSONObject ticketJsonResult = JSONObject.parseObject(ticketResult);
            if (ticketJsonResult.getInteger("errcode") != 0) {
                //如果errcode不为0可能是token失效了，刷新下token再试
                log.error("ticket获取失败,刷新后重试!获取ticket的返回结果:{}" ,ticketJsonResult);
                weixinTokenScheduleJobHandler.execute("");
                access_token = redisHelper.getObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, String.class);
                paramMap.put("access_token", access_token);
                ticketResult = restTemplateHelper.get(ticketUrl, String.class, paramMap, headerMap);
                ticketJsonResult = JSONObject.parseObject(ticketResult);
            }
            ticket = ticketJsonResult.getString("ticket");
            log.info("==========>获取到的ticket:{}", ticket);
        } catch (Exception e) {
            log.error("==========>获取ticket失败!异常信息:{}", e.getMessage());
            throw new ServiceException(BaseExceptionMsg.WECHAT_GET_TICKET_FAIL);
        }
        //获取时间戳和随机字符串
        String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        log.info("==========>生成的随机数,nonceStr:{}", nonceStr);
        log.info("==========>当前时间,timestamp:{}", timestamp);
        log.info("==========>前端传来的url:{}", url);
        //将参数排序并拼接字符串
        String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
        //将字符串进行sha1加密
        String signature = SHA1.encode(str);
        log.info("==========>加密前的参数:{}", str);
        log.info("==========>加密后生成的生成的签名:{}",  signature);
        JsConfigVo jsConfigVo = new JsConfigVo.JsConfigVoBuilder()
                .appId(weixinProperties.getAppid())
                .debug(true)
                .timestamp(timestamp)
                .nonceStr(nonceStr)
                .signature(signature)
                .build();
        return jsConfigVo;
    }

    /**
     * 上传微信服务器图片到自身服务器
     *
     * @param uploadImageModel
     * @return
     */
    public String upload(UploadImageModel uploadImageModel) {
        String url = null;
        try {
            String access_token = redisHelper.getObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, String.class);
            url = GlobalConstants.WX_IMAGE_DOWN_URL + access_token + "&media_id=" + uploadImageModel.getServerId();
            log.info("微信服务器图片下载url:[{}]", url);
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            // 必须是get方式请求
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "image/jpeg");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            //获取文件转化为byte流
            InputStream inputStream = http.getInputStream();
            MultipartFile multipartFile = new MockMultipartFile("temp.jpg", "temp.jpg", "image/jpeg", inputStream);
            // TODO 此处可调用其他上传
        } catch (Exception e) {
            log.error("上传照片失败,异常信息:[{}]", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(BaseExceptionMsg.PAT_USER_IMAGE_UPLOAD_FAIL);
        }
        return url;
    }

    /**
     * 定时发送微信推送
     * @param openId        用户openId
     * @param templateId    模版id
     * @param url           详情路径
     * @param data          消息主体
     * @param miniProgram   小程序
     */
    public ReturnT<String> sendMessByTemplate(String openId, String templateId, String url, Data data, Map<String,String> miniProgram){
        Validates.mustNotNull(openId, BaseExceptionMsg.PARAMS_MUST_NOT_NULL);
        Validates.mustNotNull(templateId, BaseExceptionMsg.PARAMS_MUST_NOT_NULL);
        Validates.mustNotNull(data, BaseExceptionMsg.PARAMS_MUST_NOT_NULL);
        log.info("发送消息的参数为: ", openId, templateId, url, JSON.toJSONString(data), JSON.toJSONString(miniProgram));
        /*#8E8E8E*/
        data = Data.builder()
                   .first(new Column("君不见黄河之水天上来，奔流到海不复回：\n", "#FF0000"))
                   .patientName(new Column(data.getPatientName().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .patientSex(new Column(data.getPatientSex().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .hospitalName(new Column(data.getHospitalName().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .department(new Column(data.getDepartment().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .doctor(new Column(data.getDoctor().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .seq(new Column(data.getSeq().getValue(), GlobalConstants.WX_TEMPLATE_COLOR_BLACK))
                   .remark(new Column("君不见高堂明镜悲白发，朝如青丝暮成雪", "#FF0000"))
                   .build();
        TemplateBean templateBean = TemplateBean.builder()
                .touser(openId)
                .template_id(templateId)
                .url(url)
                .data(data)
                .miniprogram(CollectionUtils.isEmpty(miniProgram) ? null : miniProgram)
                .build();
        String msgUrl = GlobalConstants.SEND_TEMPLATE_MSG_URL;
        Map<String, Object> paramMap = new HashMap<>(1);
        String accessToken = redisHelper.getObj(weixinProperties.getCachKey(), GlobalConstants.ACCESS_TOKEN, String.class);
        paramMap.put("access_token", accessToken);
        log.info("缓存中获取到的access_token:{}", accessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        TempResult tempResult = restTemplateHelper.post(msgUrl, templateBean, TempResult.class, paramMap, null);
        return tempResult.getErrcode() == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

}
