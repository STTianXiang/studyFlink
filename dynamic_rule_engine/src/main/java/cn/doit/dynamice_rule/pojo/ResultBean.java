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


    //  规则中的触发条件
    //private RuleAtomicParams triggerParam;

    ////  规则中的用户画像条件
    //private HashMap<String,String> userProfileParams;

    ////  规则中的行为次数条件
    //private List<RuleAtomicParams> userActionCountParams;
    //// 因为每一个事件的次数不一样.所以我们把次数塞进 List<RuleAtomicParams> 中了

    ////  规则中的行为次序类条件
    //private List<RuleAtomicParams> userActionSequenceParams;
    //而行为是一个整体(N个行为组成).. 这个整体完成到了第几步..
    //如果我也像行为次数一样塞到 List<RuleAtomicParams> 里面去,其实就是每个行为都得塞..
    // 所以就塞到这里的外层

    //  用于记录查询服务所返回的序列中匹配的最大步骤号
    private int userActionSequenceQueriedMaxStep;//比如所我要求依次完成 A->B->C 三个事件.那么我最大步骤是 3
}
