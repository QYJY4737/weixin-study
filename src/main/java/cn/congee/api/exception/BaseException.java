package cn.congee.api.exception;

import lombok.Data;

/**
 * 异常基类
 *
 * @Author: yang
 * @Date: 2020-12-09 3:34
 */
@Data
public class BaseException extends RuntimeException {

    private Integer code;
    private String message;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(IExceptionMsg exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.message = exception.getMessage();
    }

    public BaseException(Exception e) {
        super(e.getMessage());
        this.code = BaseExceptionMsg.EXECUTE_FAILD.getCode();
        this.message = e.getMessage();
    }

    public BaseException(String errMessage) {
        super(errMessage);
        this.code = BaseExceptionMsg.EXECUTE_FAILD.getCode();
        this.message = errMessage;
    }

}
