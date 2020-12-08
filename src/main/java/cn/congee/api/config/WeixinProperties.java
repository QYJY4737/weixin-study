package cn.congee.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: yang
 * @Date: 2020-12-09 3:21
 */
@Setter
@Getter
@ConfigurationProperties(prefix = WeixinProperties.WX_PREFIX)
@Configuration
public class WeixinProperties {

    public static final String WX_PREFIX = "weixin";

    /**
     * 获取access_token,默认client_credential
     */
    public String grant_type = "client_credential";

    /**
     * 第三方用户唯一凭证
     */
    public String appid;

    /**
     * 第三方用户唯一凭证密钥，即appsecret
     */
    public String secret;

    /**
     * 缓存access_token 的key
     */
    public String cachKey;

    /**
     * 自定义token
     */
    public String token;

    /**
     * 云患者端url
     */
    public String patUrl;

    public String managerUrl;

    /**
     * 地址
     */
    public String apiPath;

    public WeixinProperties() {
    }

}
