package cn.congee.api.template;

import lombok.*;

import java.io.Serializable;

/**
 * 消息模板实体类
 *
 * @Author: yang
 * @Date: 2020-12-09 6:38
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Data implements Serializable {

    private static final long serialVersionUID = -2538180522855194858L;

    private Column first;
    private Column keyword1;
    private Column keyword2;
    private Column keyword3;
    private Column keyword4;
    private Column keyword5;
    private Column keyword6;
    private Column remark;

    private Column patientName;
    private Column patientSex;
    private Column hospitalName;
    private Column department;
    private Column doctor;
    private Column seq;
    private Column test;
    private Column testdate;

    private Column deptname;
    private Column doctorname;

}
