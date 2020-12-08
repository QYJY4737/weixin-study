package cn.congee.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: yang
 * @Date: 2020-12-09 3:56
 */
@Setter
@Getter
@ToString
public class AuthUser {

    ApiKeyContent apiKeyContent;
    String appVersion;
    String accessToken;
    Long hosId;
    Long userId;

    /**
     * 登录类型（GlobalConstants）1 controller 2 pc 3 超级客户度
     */
    Integer loginType;

}
