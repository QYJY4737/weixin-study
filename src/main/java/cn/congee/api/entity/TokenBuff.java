package cn.congee.api.entity;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 给前端返回，加上JwtSubject信息，
 * 方便对不同的token进行逻辑处理
 *
 * @Author: yang
 * @Date: 2020-12-09 4:03
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenBuff {

    public final static String SPLIT = "&&&&&";
    private final static int SPLIT_SIZE = 4;
    /**
     * 用户ID
     */
    Long userId;

    /**
     * 用户分类
     */
    Integer userCategory;

    /**
     * 登录终端　1：controller 2：pc 3：超级客户度
     */
    Integer loginTerminal;

    /**
     * token
     */
    String userToken;


    public TokenBuff(JwtSubject jwtSubject, String userToken) {
        this.userId = jwtSubject.getUserId();
        this.userCategory = jwtSubject.getUserCategory();
        this.loginTerminal = jwtSubject.getLoginTerminal();
        this.userToken = userToken;
    }

    public TokenBuff(String tokenStr) {
        if (StringUtils.isBlank(tokenStr)) {
            throw new ServiceException(GlobalConstants.HTTP_HEADER_TOKEN + " 不能为空");
        }
        String[] temp = tokenStr.split(SPLIT);
        if (temp.length != SPLIT_SIZE) {
            throw new ServiceException(GlobalConstants.HTTP_HEADER_TOKEN + "格式无效");
        }
        this.userId = Long.parseLong(temp[0]);
        this.userCategory = Integer.parseInt(temp[1]);
        this.loginTerminal = Integer.parseInt(temp[2]);
        this.userToken = temp[3];
    }

    /**
     * 获取token前缀，用于删除token
     *
     * @param loginType
     * @return
     */
    public String getPrefix(Integer loginType) {
        return "" + userId + SPLIT + userCategory + SPLIT + loginType;
    }

    @Override
    public String toString() {
        return "" + userId + SPLIT + userCategory + SPLIT + loginTerminal + SPLIT + userToken;
    }

}
