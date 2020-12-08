package cn.congee.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 微信签名返回vo
 *
 * @Author: yang
 * @Date: 2020-12-09 6:19
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JsConfigVo implements Serializable {

    private static final long serialVersionUID = 6538254391702708321L;

    private boolean debug;

    /**
     * appId
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 随机数
     */
    private String nonceStr;

    /**
     * 微信加密签名
     */
    private String signature;

    public static class JsConfigVoBuilder {
        private boolean debug;
        private String appId;
        private String timestamp;
        private String nonceStr;
        private String signature;

        public JsConfigVoBuilder() {
        }

        public JsConfigVo.JsConfigVoBuilder debug(final boolean debug) {
            this.debug = debug;
            return this;
        }

        public JsConfigVo.JsConfigVoBuilder appId(final String appId) {
            this.appId = appId;
            return this;
        }

        public JsConfigVo.JsConfigVoBuilder timestamp(final String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public JsConfigVo.JsConfigVoBuilder nonceStr(final String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public JsConfigVo.JsConfigVoBuilder signature(final String signature) {
            this.signature = signature;
            return this;
        }

        public JsConfigVo build() {
            return new JsConfigVo(this.debug, this.appId, this.timestamp, this.nonceStr, this.signature);
        }

    }

}
