package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**
 * @author not_today
 * @Date 2021/7/27 22:29
 * @Description
 *      因为我的行为次数数据都是在Flink的State中进行统计的..
 *          所以叫做StateImpl
 */
public class UserActionCountQueryServiceStateImpl implements UserActionCountQueryService {

    //还是只需要考虑.. 你给我什么..我给你什么..
        //  既然我的数据是在State中. 那么你就需要给我State.然后我会返回给你次数.
    /** 但是这个次数不好返回..一次返回一个? 还是说返回一个List? 或者一个Map:Key是事件,次数是value
     *      也不行..我的查询条件如下. 如果我的事件类型相同.只是里面的属性条件不同..那么返回Map,就会变成一个..比如  U(p1=v3,p2=v2)>=3,U(p3=v1,p4=v4)>=2;
     *      所以我这里 在 RuleAtomicParam 规则参数中的原子条件封装实体类中加入 realCnts字段 用于记录查询服务所返回的查询值*/
    //行为日志中且做过 U事件(且 p1=v3,p2=v2) >=3次 并且 G(p6=v8,p4=v5,p1=v2)>=1次

    //至于这里的返回值,还是返回boolean, 我告诉你次数是否达到,并且回写他做了几次.
    @Override
    public boolean queryActionCounts(ListState<LogBean> eventState, RuleParam ruleParam){


        return false;
    }
}
