package cn.congee.api.constants;

/**
 * 常量定义
 *
 * @Author: yang
 * @Date: 2020-12-09 3:59
 */
public final class GlobalConstants {

    //编码
    public static final String CHARSET = "UTF-8";

    //aes 密码
    public static final String AES_PWD = "4ljCFxBK1d3DT9qX";

    //aes 偏移量
    public static final String AES_IV = "qfepDr6RFWy5RDc2";

    public static final String APP_ENTRY_SWIFT = "swift";

    //默认的apiKey
    public static final String API_KEY_DEFAULT = "5faadbf7-7200-42b2-8466-acdb7ee349e6";
    public static final String DEFAULT_APP_VERSION = "1.0";

    //HTTP 请求参数
    public static final String HTTP_HEADER_APIKEY = "X-Api-Key";
    public static final String HTTP_HEADER_APP_VERSION = "X-Api-Ver";
    public static final String HTTP_HEADER_TOKEN = "X-Auth-Token";
    public static final String HTTP_HEADER_USERID = "X-User-Id";
    public static final String HTTP_HEADER_HOSID = "X-Hos-Id";
    public static final String HTTP_HEADER_LOGIN_TYPE = "Login-Type";
    public static final String HTTP_HEADER_INTERNAL = "X-Swift-Inter";
    public static final String HTTP_HEADER_ENTRY = "X-Swift-Entry";

    //用户缓存
    public final static String CACHE_USER = "loginuser";

    //标识来源网关的请求，区分请求是来源网关（用户）｜微服务之间的请求
    public static final String REQUEST_GW = "Request-Gw";

    //请求号
    public static final String REQUEST_NO = "Request-No";

    //获取微信官方access_token的url
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    //获取access_token的key值
    public static final String ACCESS_TOKEN = "access_token";

    //access_token 缓存时间
    public static final long ACCESS_TOKEN_EXPERI_SECONDS = 7200;

    //获取openId的url
    public static final String WX_OPENID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    //获取ticket的url
    public static final String WX_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    //上传图片的url
    public static final String WX_IMAGE_DOWN_URL = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=";

    //模板消息 字体颜色  黑色
    public static final String WX_TEMPLATE_COLOR_BLACK = "#000000";

    //发送模板消息的微信url
    public static final String SEND_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    //公安系统校验身份证 app_key
    public static final String CER_APP_KEY = "8a4b302787b1bbf12d58db56ab1c307f";

    //公安系统校验身份证url
    public static final String CER_URL = "http://op.juhe.cn/idcard/query";

}
