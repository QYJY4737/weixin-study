package cn.congee.api.template;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * 微信推送消息模板
 *
 * @Author: yang
 * @Date: 2020-12-09 6:46
 */
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TemplateBean implements Serializable {

    private static final long serialVersionUID = 8037183156298789772L;

    /**
     * openId
     */
    private String touser;
    /**
     * 消息模板id
     */
    private String template_id;
    /**
     * 详情路径
     */
    private String url;
    /**
     * 颜色
     */
    private String topcolor ="#FF0000";
    /**
     * 消息主体
     */
    private Data data;

    /**
     * 小程序
     */
    private Map<String, String> miniprogram;

}
