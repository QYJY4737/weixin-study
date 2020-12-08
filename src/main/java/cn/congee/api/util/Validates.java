package cn.congee.api.util;

import cn.congee.api.exception.IExceptionMsg;
import cn.congee.api.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * 校验参数工具类
 *
 * @Author: yang
 * @Date: 2020-12-09 6:40
 */
@Slf4j
public final class Validates {

    private Validates() {
    }

    public static void isTrue(final boolean expression, IExceptionMsg msg) {
        isTrue(expression, msg, msg.getMessage());
    }

    public static void isTrue(final boolean expression, ServiceException e) {
        if (expression) {
            throw e;
        }
    }

    public static void isTrue(final boolean expression, String e) {
        if (expression) {
            throw new ServiceException(e);
        }
    }

    public static void isTrue(final boolean expression, String e,String message) {
        if (expression) {
            log.error(message);
            throw new ServiceException(e);
        }
    }

    public static void isTrue(final boolean expression, IExceptionMsg msg, String logMsg) {
        try {
            Validate.isTrue(!expression);
        } catch (Exception e) {
            log.error("错误信息:{}", e.getMessage());
            log.error("[{}]", logMsg);
            throw new ServiceException(msg.getCode(), logMsg);
        }
    }

    public static <T> T mustNotNull(final T object, IExceptionMsg msg) {
        return mustNotNull(object, msg, msg.getMessage());
    }


    public static <T> T mustNotNull(final T object, IExceptionMsg msg, String logMsg) {
        if (object == null) {
            log.error("{}", logMsg);
            throw new ServiceException(msg.getCode(), logMsg);
        }
        if (object instanceof String && StringUtils.isBlank(String.valueOf(object))) {
            log.error("{}", logMsg);
            throw new ServiceException(msg.getCode(), logMsg);
        }
        return object;
    }

    public static void logError(boolean expression, String msg, Object... arguments) {
        if (expression) {
            log.error(msg, arguments);
        }
    }

}
