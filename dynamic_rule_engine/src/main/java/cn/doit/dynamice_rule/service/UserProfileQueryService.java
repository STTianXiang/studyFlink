package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.RuleParam;

/**
 * @author not_today
 * @Date 2021/7/27 22:15
 * @Description     用户画像数据查询服务..
 *      version 1.1  因为在 RuleEngineV1 中我的查询条件都是写死的..真正生产中查询条件可能是非常复杂的..
 *          而且现在我们的用户画像是存在Hbase中的.. 但是实际上.可能存在其他地方.比如说Mysql之类的地方..
 *      所以我这里把这个查询服务定义成接口.. 将来你的画像存在那里.自己实现..
 */
public interface UserProfileQueryService {

    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam);
    
}
