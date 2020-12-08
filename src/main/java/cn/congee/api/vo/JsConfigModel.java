package cn.congee.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yang
 * @Date: 2020-12-09 6:21
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JsConfigModel implements Serializable {

    /**
     * 获取签名需要传参 url,即前端服务器的ip地址
     */
    @NotBlank(message = "url 不能为空!")
    private String url;

}
