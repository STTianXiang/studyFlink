package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**     version 1.1
 *      目前 只有3个条件.. 一个是查画像,一个是查次数,一个是查次序..
 *     这个是查次序的接口
 */

public interface UserActionSequenceQueryService {

    public boolean queryActionSequence(ListState<LogBean> eventState, RuleParam ruleParam);
}
