package cn.doit.dynamice_rule.tools;

import cn.doit.dynamice_rule.pojo.RuleAtomicParam;
import cn.doit.dynamice_rule.pojo.RuleParam;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author not_today
 * @Date 2021/8/8 12:48
 * @Description 规则模拟器.最终版本是希望产品或运营在页面配置条件规则. 现在先在这里规则模拟器中定义 静态的规则
 */
public class RuleSimulator {

    public static RuleParam getRuleParam(){

        RuleParam ruleParam = new RuleParam();

        ruleParam.setRuleId("rule_test_1");

        RuleAtomicParam trigger = new RuleAtomicParam();


        // 设置触发条件规则
        trigger.setEventId("E");
        ruleParam.setTriggerParam(trigger);

        // 构造画像条件规则
        HashMap<String, String> userProfile = new HashMap<>();
        userProfile.put("tag12","v92");
        userProfile.put("tag22","v3");
        ruleParam.setUserProfileParams(userProfile);


        //构造次数条件规则
        RuleAtomicParam count1 = new RuleAtomicParam();
        count1.setEventId("B");
        HashMap<String, String> countParam1 = new HashMap<>();
        countParam1.put("p1","v1");
        count1.setProperties(countParam1);
        count1.setRangeStart(-1);
        count1.setRangeEnd(-1);
        count1.setCnts(2);



        RuleAtomicParam count2 = new RuleAtomicParam();
        count2.setEventId("D");
        HashMap<String, String> countParam2 = new HashMap<>();
        countParam2.put("p2","v3");
        count2.setProperties(countParam2);
        count2.setRangeStart(-1);
        count2.setRangeEnd(-1);
        count2.setCnts(2);

        ArrayList<RuleAtomicParam> countParam = new ArrayList<>();
        countParam.add(count1);
        countParam.add(count2);
        ruleParam.setUserActionCountParams(countParam);


        //构造次序条件规则
        RuleAtomicParam sequence1 = new RuleAtomicParam();
        sequence1.setEventId("A");
        HashMap<String, String> seq1 = new HashMap<>();
        seq1.put("p1","v1");
        sequence1.setProperties(seq1);

        RuleAtomicParam sequence2 = new RuleAtomicParam();
        sequence2.setEventId("C");
        HashMap<String, String> seq2 = new HashMap<>();
        seq2.put("p2","v2");
        sequence2.setProperties(seq2);


        ArrayList<RuleAtomicParam> sequence = new ArrayList<>();
        sequence.add(sequence1);
        sequence.add(sequence2);

        ruleParam.setUserActionSequenceParams(sequence);


        //返回规则
        return ruleParam;

    }
}
