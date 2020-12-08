package cn.congee.api.entity;

import cn.congee.api.constants.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.security.auth.message.AuthException;

/**
 * Token信息
 *
 * @Author: yang
 * @Date: 2020-12-09 4:04
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ControllerAdvice
public class JwtSubject {

    public static final int INT_3 = 3;
    /**
     * 用户分类
     */
    Integer userCategory;

    /**
     * 用户ID
     */
    Long userId;

    /**
     * 登录终端　1：controller 2：pc 3：超级客户度
     */
    Integer loginTerminal;


    /**
     * 记录refreshToken,用于登出|刷新token情况的老的refreshToken
     */
    String refreshToken;

    /**
     * 过期时间，用于判断是否已过期
     */
    Long expireTime;

    public JwtSubject(Integer userCategory, Long userId, Integer loginTerminal) {
        this.userCategory = userCategory;
        this.userId = userId;
        this.loginTerminal = loginTerminal;
    }

    public JwtSubject(String userToken) {
        if (StringUtils.isBlank(userToken)) {
            try {
                throw new AuthException(GlobalConstants.HTTP_HEADER_TOKEN + " 不能为空");
            } catch (AuthException e) {
                e.printStackTrace();
            }
        } else {
            String[] temp = userToken.split(TokenBuff.SPLIT);
            if (temp.length != INT_3) {
                log.error("userToken format invalid,token={}", userToken);
                try {
                    throw new AuthException(GlobalConstants.HTTP_HEADER_TOKEN + "格式无效");
                } catch (AuthException e) {
                    e.printStackTrace();
                }
            }
            this.userId = Long.parseLong(temp[0]);
            this.userCategory = Integer.parseInt(temp[1]);
            this.loginTerminal = Integer.parseInt(temp[2]);
        }
    }

    @Override
    public String toString() {
        return userCategory + "::" + userId + "::" + loginTerminal;
    }

}
