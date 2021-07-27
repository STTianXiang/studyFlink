package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**
 * @author not_today
 * @Date 2021/7/27 22:52
 * @Description
 *      因为我是行为数据是放在Flink的State中的.
 */
public class UserActionSequenceQueryServiceStateImpl implements UserActionSequenceQueryService {

    /**
     *   这个查询如果只返回 true 和 false 未免会丢失很多信息.比如说
     *      我的次序要求条件是依次做过  A -> B -> C  三个事件.
     *      如果我只做到了B, 只返回一个false.. 但这个用户产品大概率也会需要做进一步的诱导消费..所以最好也是需要的
     *    我这里可以返回一个 该用户最后做到第几步,比如说我这里的ABC是三步, 如果只做到了B.我就返回一个2..
     *   下次该用户又做了C.. 可以在加上第3步..
     *   可以照猫画虎 参照查询次数.. 我可以返回是否满足 次序条件要求(返回true或false) 以及回写最大步骤
     *     这里我是回写到了 RuleParam中 而不是RuleAtomicParam
     */
    @Override
    public boolean queryActionSequence(ListState<LogBean> eventState, RuleParam ruleParam){


        return false;
    }
}
