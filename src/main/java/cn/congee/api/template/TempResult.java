package cn.congee.api.template;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: yang
 * @Date: 2020-12-09 6:49
 */
@Setter
@Getter
public class TempResult implements Serializable {

    private static final long serialVersionUID = -8520752826042694320L;
    private Integer errcode;
    private String errmsg;
    private Long msgid;

}
