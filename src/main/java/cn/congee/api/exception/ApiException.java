package cn.congee.api.exception;

/**
 * 远程接口调用出现的业务异常
 *
 * @Author: yang
 * @Date: 2020-12-09 3:53
 */
public class ApiException extends BaseException {

    public ApiException(Integer code, String errorMessage) {
        super(code, errorMessage);
    }

    public ApiException(IExceptionMsg exception) {
        super(exception);
    }

    public ApiException(Exception e) {
        super(e);
    }

    public ApiException(String errMessage) {
        super(errMessage);
    }

}
