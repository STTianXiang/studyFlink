package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleAtomicParam;
import cn.doit.dynamice_rule.pojo.RuleParam;
import cn.doit.dynamice_rule.tools.RuleCalcUtil;
import org.apache.flink.api.common.state.ListState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author not_today
 * @Date 2021/7/27 22:29
 * @Description
 *      因为我的行为次数数据都是在Flink的State中进行统计的..
 *          所以叫做StateImpl
 */

//首先我们的这个服务是用于来查询次数条件是否满足.次数条件是某个事件做过几次,并且每个事件之中还有原子条件都得满足才算true.

public class UserActionCountQueryServiceStateImpl implements UserActionCountQueryService {

    //还是只需要考虑.. 你给我什么..我给你什么..
        //  既然我的数据是在State中. 那么你就需要给我State.然后我会返回给你次数.
    /** 但是这个次数不好返回..一次返回一个? 还是说返回一个List? 或者一个Map:Key是事件,次数是value
     *      也不行..我的查询条件如下. 如果我的事件类型相同.只是里面的属性条件不同..那么返回Map,就会变成一个..比如  U(p1=v3,p2=v2)>=3,U(p3=v1,p4=v4)>=2;
     *      所以我这里 在 RuleAtomicParam 规则参数中的原子条件封装实体类中加入 realCnts字段 用于记录查询服务所返回的查询值*/
    //行为日志中且做过 U事件(且 p1=v3,p2=v2) >=3次 并且 G(p6=v8,p4=v5,p1=v2)>=1次

    //至于这里的返回值,还是返回boolean, 我告诉你次数是否达到,并且回写他做了几次.
    @Override
    public boolean queryActionCounts(ListState<LogBean> eventState, RuleParam ruleParam) throws Exception {

        //取出规则条件中用户行为次数条件
        List<RuleAtomicParam> userActionCountParams = ruleParam.getUserActionCountParams();

        //首先迭代State获得每一个历史事件明细
        Iterable<LogBean> beansIterable = eventState.get();//State并不能直接迭代.因为它是Flink中的对象,并不是Java中的类
        /** 这里可以直接抛出异常,因为你是在Flink的state中获取数据,如果这都报错了.说明Flink出问题了. */

        //统计每一个原子条件所发生的真实次数,并且回写到条件参数对象中:realCnts;
        queryActionCountsHelper(beansIterable, userActionCountParams);

        for (RuleAtomicParam userActionCountParam : userActionCountParams) {

            //每一个原子条件中的真实值 如果大于或等于 阈值 则满足条件. 我这里是反过来写..如果某一个原子条件不满足,我就直接返回false了..因为我要求的是必须所有原子条件都满足.
            if (userActionCountParam.getRealCnts() < userActionCountParam.getCnts()){
                return false;
            }
        }
        return true;//如果到这里,说明上面的判断中,每个原子条件都满足,返回整体结果true
    }

    /** 为了容易做单元测试,我在这里新写一个方法,因为上面的方法如果要做测试,需要传入一个State,需要启动Flink */
    public void queryActionCountsHelper(Iterable<LogBean> beansIterable,List<RuleAtomicParam> userActionCountParams){

        for (LogBean logBean : beansIterable) {
            for (RuleAtomicParam userActionCountParam : userActionCountParams) {
                //判断当前logbean 和 当前规则原子条件userActionCountParam 是否一致.
                boolean isMatch = RuleCalcUtil.eventBeanMatchActionParam(logBean, userActionCountParam);
                //如果一致则次数+1
                if (isMatch){
                    userActionCountParam.setRealCnts(userActionCountParam.getRealCnts()+1);
                }
            }
        }
    }

    // version 1.1中因为查询次序服务也需要这个方法,所以把类似这种具有公共能力的方法放入 tools 中
    //内部工具方法:用于判断一个事件和一个规则中的原子条件是否一致
    /*private boolean eventBeanMatchActionParam(LogBean eventBean,RuleAtomicParam eventParam){
        //参数对象 和 事件对象 是否符合条件
        if (eventBean.getEventId().equals(eventParam.getEventId())){
            //如果传入的一个事件的事件id与参数中的事件id相同,就进行属性判断
            HashMap<String, String> paramProperties = eventParam.getProperties();//取出条件中的事件属性
            Set<Map.Entry<String, String>> entries = paramProperties.entrySet();

            Map<String, String> eventProperties = eventBean.getProperties();//取出等待进行判断事件中的属性

            for (Map.Entry<String, String> entry : entries) { //遍历条件中的每个属性值
                if (!entry.getValue().equals(eventProperties.get(entry.getKey()))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }*/
}
