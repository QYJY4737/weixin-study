package cn.congee.api.helper;

import cn.congee.api.constants.GlobalConstants;
import cn.congee.api.entity.ApiKeyContent;
import cn.congee.api.entity.AuthUser;
import cn.congee.api.entity.JwtSubject;
import cn.congee.api.entity.TokenBuff;
import cn.congee.api.exception.ApiException;
import cn.congee.api.exception.BaseExceptionMsg;
import cn.congee.api.util.AesUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证工具类
 *
 * @Author: yang
 * @Date: 2020-12-09 3:57
 */
@Slf4j
public class RequestHelper {

    /**
     * 获取当前请求的Request对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        } else {
            return requestAttributes.getRequest();
        }
    }

    /**
     * 获取当前请求的Response对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        } else {
            return requestAttributes.getResponse();
        }
    }

    /**
     * 获取httpHeader认证信息
     *
     * @return
     */
    public static AuthUser getAuthUser() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            AuthUser authUser = new AuthUser();
            authUser.setApiKeyContent(getApiKeyContent(request));
            authUser.setAppVersion(getAppVersion());
            authUser.setAccessToken(getUserToken(request));
            authUser.setHosId(getHosId(request));
            authUser.setUserId(getUserId(request));
            authUser.setLoginType(getLoginType());
            return authUser;
        }
        return null;
    }

    /**
     * 获取请求API-KEY
     *
     * @param request
     * @return
     */
    public static ApiKeyContent getApiKeyContent(HttpServletRequest request) {
        String apikeyJson = request.getHeader(GlobalConstants.HTTP_HEADER_APIKEY);
        if (StringUtils.isBlank(apikeyJson)) {
            apikeyJson = request.getParameter(GlobalConstants.HTTP_HEADER_APIKEY);
        }
        if (StringUtils.isBlank(apikeyJson)) {
            return null;
        } else {
            try {
                log.info("请求header[X-Api-Key]={}", apikeyJson);
                String json = AesUtils.decrypt(apikeyJson, GlobalConstants.AES_PWD, GlobalConstants.AES_IV);
                return JSON.parseObject(json, ApiKeyContent.class);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("请求header[X-Api-Key]字段不合法，apikey={}", apikeyJson);
                return null;
            }
        }
    }

    /**
     * 获取请求UserId
     *
     * @return
     */
    public static Long getUserId() {
        HttpServletRequest request = getRequest();
        return getUserId(request);
    }

    /**
     * 获取请求UserId
     *
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request) {
        String userId = request.getHeader(GlobalConstants.HTTP_HEADER_USERID);
        if (StringUtils.isBlank(userId)) {
            userId = request.getParameter(GlobalConstants.HTTP_HEADER_USERID);
        }
        String token = request.getHeader(GlobalConstants.HTTP_HEADER_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(GlobalConstants.HTTP_HEADER_TOKEN);
        }
        if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(token)) {
            log.info("从token[{}]中获取userId", token);
            userId = token.split(TokenBuff.SPLIT)[0];
        }
        if (StringUtils.isBlank(userId)) {
            //从token中获取
            if (StringUtils.isBlank(token)) {
                return -1L;
            }
            RedisHelper redisHelper = SpringContextHelper.getBean("redisHelper");
            if (redisHelper == null) {
                log.error("获取RedisHelper对象失败");
                throw new ApiException(BaseExceptionMsg.SPRINGCONTEXTHELPER_ERROR);
            } else {
                JwtSubject jwtSubject = redisHelper.getObj(GlobalConstants.CACHE_USER, token, JwtSubject.class);
                if (jwtSubject != null) {
                    return jwtSubject.getUserId();
                }
            }
        }
        if (StringUtils.isBlank(userId)) {
            return -1L;
        }
        Long temp = -1L;
        try {
            if (NumberUtils.isNumber(userId)) {
                temp = Long.parseLong(userId);
            }
        } catch (Exception e) {
            log.error("请求header[X-User-Id]字段不合法，userId={}", userId);
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 获取请求userToken
     *
     * @param request
     * @return
     */
    public static String getUserToken(HttpServletRequest request) {
        String userToken = request.getHeader(GlobalConstants.HTTP_HEADER_TOKEN);
        if (StringUtils.isNotBlank(userToken)) {
            return userToken;
        } else {
            return request.getParameter(GlobalConstants.HTTP_HEADER_TOKEN);
        }
    }

    /**
     * 获取请求hosId
     *
     * @return
     */
    public static Long getHosId(HttpServletRequest request) {
        String hosId = request.getHeader(GlobalConstants.HTTP_HEADER_HOSID);
        if (StringUtils.isBlank(hosId)) {
            hosId = request.getParameter(GlobalConstants.HTTP_HEADER_HOSID);
        }
        if (StringUtils.isBlank(hosId)) {
            return -1L;
        }
        Long temp = -1L;
        try {
            temp = Long.parseLong(hosId);
        } catch (Exception e) {
            log.error("请求header[X-Hos-Id]字段不合法，hosId={}", hosId);
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 获取请求 app版本
     *
     * @return
     */
    public static String getAppVersion() {
        HttpServletRequest request = getRequest();
        String appversion = request.getHeader(GlobalConstants.HTTP_HEADER_APP_VERSION);
        if (StringUtils.isBlank(appversion)) {
            appversion = request.getParameter(GlobalConstants.HTTP_HEADER_APP_VERSION);
        }
        return appversion;
    }

    /**
     * 获取登录类型(1 pc 2 app)
     *
     * @return
     */
    public static Integer getLoginType() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String loginType = request.getHeader(GlobalConstants.HTTP_HEADER_LOGIN_TYPE);

        //兼容header传null字符串问题，下版本修改
        if (!StringUtils.isBlank(loginType) && loginType.equals("null")) {
            return null;
        }

        if (StringUtils.isBlank(loginType)) {
            loginType = request.getParameter(GlobalConstants.HTTP_HEADER_LOGIN_TYPE);
        }
        if (StringUtils.isBlank(loginType)) {
            String token = request.getHeader(GlobalConstants.HTTP_HEADER_TOKEN);
            if (StringUtils.isBlank(token)) {
                return null;
            }
            String[] split = token.split(TokenBuff.SPLIT);
            if (split.length != 4) {
                return null;
            }
            return Integer.valueOf(split[2]);
        }
        return loginType == null ? null : Integer.parseInt(loginType);
    }

    /**
     * 获取请求内部标识
     *
     * @return
     */
    public static String getInternalSign() {
        String requestSource = getRequest().getHeader(GlobalConstants.HTTP_HEADER_INTERNAL);
        if (StringUtils.isNotBlank(requestSource)) {
            return requestSource;
        } else {
            return getRequest().getParameter(GlobalConstants.HTTP_HEADER_INTERNAL);
        }
    }

    /**
     * 获取微服务请求来源
     *
     * @param request
     * @return
     */
    public static String getRequestSource(HttpServletRequest request) {
        String requestSource = request.getHeader(GlobalConstants.REQUEST_GW);
        if (StringUtils.isNotBlank(requestSource)) {
            return requestSource;
        } else {
            return request.getParameter(GlobalConstants.REQUEST_GW);
        }
    }

    /**
     * 获取微服务请求来源
     *
     * @param request
     * @return
     */
    public static String getRequestNo(HttpServletRequest request) {
        String requestSource = request.getHeader(GlobalConstants.REQUEST_NO);
        if (StringUtils.isNotBlank(requestSource)) {
            return requestSource;
        } else {
            return request.getParameter(GlobalConstants.REQUEST_NO);
        }
    }

    /**
     * 获取请求的ip地址
     */
    public static String getRequestIp() {
        HttpServletRequest request = RequestHelper.getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip)
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip)
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip)
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip)
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
