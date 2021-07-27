package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.RuleParam;

/**
 * @author not_today
 * @Date 2021/7/27 22:20
 * @Description
 *      UserProfileQueryService 接口的实现类.. 用于查询Hbase中的画像数据..
 */
public class UserProfileQueryServiceHbaseImpl implements UserProfileQueryService {

    //你给我什么. 我给你什么  -> 这一块只需要实现这个逻辑
       //这里需要你给我整个规则参数.我从这里面拿到画像的几个条件,以及deviceId  而这个实现类会告诉你是否满足条件

    @Override
    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam){
        //传入一个用户号,以及条件.返回是否满足

        return false;
    }

}
