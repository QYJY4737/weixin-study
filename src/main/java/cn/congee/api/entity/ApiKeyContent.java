package cn.congee.api.entity;

import lombok.*;

/**
 * @Author: yang
 * @Date: 2020-12-09 3:43
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyContent {

    private String accessEntry;
    private String apiKey;
    private Long timestamp;

}
