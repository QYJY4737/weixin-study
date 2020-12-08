package cn.congee.api.exception;

/**
 * 异常信息定义
 *
 * @Author: yang
 * @Date: 2020-12-09 3:35
 */
public interface IExceptionMsg {

    /**
     * 获取异常的状态码
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取异常的提示信息
     *
     * @return
     */
    String getMessage();

}
