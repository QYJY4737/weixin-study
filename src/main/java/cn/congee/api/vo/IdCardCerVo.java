package cn.congee.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 调用公安校验系统 返回结果vo
 *
 * @Author: yang
 * @Date: 2020-12-12 10:24
 */
@Setter
@Getter
@ToString
public class IdCardCerVo implements Serializable {

    private String idcard;
    private String realname;
    private Integer res;

}
