package cn.doit.dynamice_rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

/**
 * @author not_today
 * @Date 2021/7/24 15:50
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResultBean {
    /**
     规则的整体条件封装实体

     规则引擎:
     触发条件:埋点日志 EventId = E
     画像属性条件: k3=v3 , k100=v80 , k230=v360
     行为属性条件: U(p1=v3,p2=v2)>=3 且 G(p6=v8,p4=v5,p1=v2)>=1
     行为次序条件: 依次做过: W(p1=v4) -> R(p2=v3) -> F

     RuleAtomicParams 类中 其实只是 触发条件已经行为属性条件(原子条件)

     */

    //version 1.0版本的 ResultBean
    private String ruleId;
    private String deviceId;
    private Long   timeStamp;

}
