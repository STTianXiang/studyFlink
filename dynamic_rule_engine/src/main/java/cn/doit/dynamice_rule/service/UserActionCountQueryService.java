package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**
 * @author not_today
 * @Date 2021/7/27 22:28
 * @Description
 *    version 1.1
 *      既然有了用户画像查询接口..像类似这种服务其实都可以做成接口.
 *          这个接口来实现 行为日志次数查询服务
 */
public interface UserActionCountQueryService {

    public boolean queryActionCounts(ListState<LogBean> eventState, RuleParam ruleParam);

}
