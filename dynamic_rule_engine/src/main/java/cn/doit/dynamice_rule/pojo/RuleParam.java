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

    //  用于记录查询服务所返回的序列中匹配的最大步骤号
    private int userActionSequenceQueriedMaxStep;//比如所我要求依次完成 A->B->C 三个事件.那么我最大步骤是 3
}
