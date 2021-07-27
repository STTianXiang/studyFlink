package cn.doit.dynamice_rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author not_today
 * @Date 2021/7/25 13:40
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RuleParam implements Serializable {
    //规则整体封装实体

    //  规则中的触发条件
    private RuleAtomicParam triggerParam;

    ////  规则中的用户画像条件
    private HashMap<String,String> userProfileParams;

    ////  规则中的行为次数条件
    private List<RuleAtomicParam> userActionCountParams;
    //// 因为每一个事件的次数不一样.所以我们把次数塞进 List<RuleAtomicParams> 中了

    ////  规则中的行为次序类条件
    private List<RuleAtomicParam> userActionSequenceParams;
    //而行为是一个整体(N个行为组成).. 这个整体完成到了第几步..
    //如果我也像行为次数一样塞到 List<RuleAtomicParams> 里面去,其实就是每个行为都得塞..
    // 所以就塞到这里的外层

    // version 1.1 用于记录查询服务所返回的序列中匹配的最大步骤号
    private int userActionSequenceQueriedMaxStep;//比如所我要求依次完成 A->B->C 三个事件.那么我最大步骤是 3
    /** 上面是次数条件是回写到了 RuleAtomicParam 里面  但是如果我的次序也回写到 RuleAtomicParam 里面就会有冗余
     *    因为RuleAtomicParam里面是按照eventId 也就是事件类型为粒度, 而我的次序只是回写他完成到了第几步.没必要每个事件都写上一个最大步骤号*/
}
