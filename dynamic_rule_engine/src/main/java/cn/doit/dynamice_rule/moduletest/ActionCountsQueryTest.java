package cn.doit.dynamice_rule.moduletest;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleAtomicParam;
import cn.doit.dynamice_rule.service.UserActionCountQueryServiceStateImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author not_today
 * @Date 2021/8/1 20:55
 * @Description
 */
public class ActionCountsQueryTest {
    //行为次数查询服务功能测试
    public static void main(String[] args) {
        UserActionCountQueryServiceStateImpl service = new UserActionCountQueryServiceStateImpl();

        //构造测试用明细事件
        LogBean logBean1 = new LogBean();
        logBean1.setEventId("000010");
        HashMap<String, String> props1 = new HashMap<>();
        props1.put("p1","v1");
        logBean1.setProperties(props1);

        LogBean logBean2 = new LogBean();
        logBean2.setEventId("000010");
        HashMap<String, String> props2 = new HashMap<>();
        props2.put("p1","v2");
        logBean2.setProperties(props2);

        LogBean logBean3 = new LogBean();
        logBean3.setEventId("000020");
        HashMap<String, String> props3 = new HashMap<>();
        props3.put("p2","v3");
        logBean3.setProperties(props3);

        LogBean logBean4 = new LogBean();
        logBean4.setEventId("000020");
        HashMap<String, String> props4 = new HashMap<>();
        props4.put("p2","v3");
        props4.put("p3","v4");
        logBean4.setProperties(props4);

        ArrayList<LogBean> eventList = new ArrayList<>();
        eventList.add(logBean1);
        eventList.add(logBean2);
        eventList.add(logBean3);
        eventList.add(logBean4);

        RuleAtomicParam ruleAtomicParam1 = new RuleAtomicParam();
        ruleAtomicParam1.setEventId("000010");
        HashMap<String, String> paramProps1 = new HashMap<>();
        paramProps1.put("p1","v1");
        ruleAtomicParam1.setProperties(paramProps1);
        ruleAtomicParam1.setCnts(2);

        RuleAtomicParam ruleAtomicParam2 = new RuleAtomicParam();
        ruleAtomicParam2.setEventId("000020");
        HashMap<String, String> paramProps2 = new HashMap<>();
        paramProps2.put("p2","v3");
        ruleAtomicParam2.setProperties(paramProps2);
        ruleAtomicParam2.setCnts(2);

        ArrayList<RuleAtomicParam> ruleAtomicParams = new ArrayList<>();
        ruleAtomicParams.add(ruleAtomicParam1);
        ruleAtomicParams.add(ruleAtomicParam2);

        service.queryActionCountsHelper(eventList,ruleAtomicParams);

        for (RuleAtomicParam param : ruleAtomicParams) {

            System.out.println(param.getEventId()+"-> 阈值:"+param.getCnts()+", 次数:"+param.getRealCnts());
        }
    }
}
