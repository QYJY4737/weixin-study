package cn.congee.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yang
 * @Date: 2020-12-09 6:29
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageModel implements Serializable {

    /**
     * 微信官方素材的serverId
     */
    @NotBlank(message = "serverId不能为空!")
    private String serverId;

}
