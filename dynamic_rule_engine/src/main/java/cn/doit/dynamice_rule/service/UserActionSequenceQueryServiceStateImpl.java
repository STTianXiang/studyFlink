package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleAtomicParam;
import cn.doit.dynamice_rule.pojo.RuleParam;
import cn.doit.dynamice_rule.tools.RuleCalcUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.state.ListState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author not_today
 * @Date 2021/7/27 22:52
 * @Description 查询规则条件中的行为次序是否满足 A(p1=v2,p3=v8) -> B(P6=V9,P7=V7) -> C()
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
    public boolean queryActionSequence(ListState<LogBean> eventState, RuleParam ruleParam) throws Exception {

        Iterable<LogBean> logBeans = eventState.get();
        List<RuleAtomicParam> userActionSequenceParams = ruleParam.getUserActionSequenceParams();

        //调用 queryActionSequnceHelper 方法,统计到目前用户做的第几道步骤.
        int maxStep = queryActionSequnceHelper(logBeans, userActionSequenceParams);

        //将用户做到第几步骤写回规则参数对象中
        ruleParam.setUserActionSequenceQueriedMaxStep(maxStep);

        return maxStep==userActionSequenceParams.size();
    }

    public int queryActionSequnceHelper(Iterable<LogBean> events,List<RuleAtomicParam> userActionSequenceParams){

        ArrayList<LogBean> eventList = new ArrayList<>();
        CollectionUtils.addAll(eventList,events.iterator());

        int maxStep = 0;
        int index = 0;

        //外循环,遍历每个条件
        for (RuleAtomicParam userActionSequenceParam : userActionSequenceParams) {
            /**  这里有个问题. 如果我第一次找到了匹配(A事件步骤),也就是内循环成功给 maxStep赋值了.
             *  但是我第二次没有找到(B事件步骤),那么内循环退出后,还会继续外循环去找第三个条件(C事件步骤)
             *  所以我在外面在设置一个 boolean 变量值..来判断内循环是否成功匹配到事件
             */
            boolean isFind = false;
            //内循环,遍历每一个历史明细事件
            for (int i=index;i<eventList.size();i++){
                LogBean logBean = eventList.get(i);
                //判断当前的这个事件 logBean,是否满足当前规则条件 userActionSequenceParam
                boolean actionParam = RuleCalcUtil.eventBeanMatchActionParam(logBean, userActionSequenceParam);

                if (actionParam){
                    //如果匹配,则最大步骤+1,并且更新下一次内循环的位置(角标),并且跳出本次循环
                    maxStep++;
                    index = i + 1;
                    isFind = true;
                    break;
                }
            }
            if (!isFind) break;
        }
        return maxStep;
    }

    /***
     *
     * @param userActionSequenceParams
     * @param events
     * @return   queryActionSequnceHelper 方法的优化
     *  上面的方法是双层for循环.. 通过拿规则去和事件进行比较..
     *      可以改进为  单层for循环.. 拿事件去和规则进行比较
     *    详情可查看设计图模块
     */
    public int queryActionSequnceHelper(List<RuleAtomicParam> userActionSequenceParams,Iterable<LogBean> events){

            int maxStep = 0;   //定义规则条件的索引

        for (LogBean event : events) {
            if (RuleCalcUtil.eventBeanMatchActionParam(event,userActionSequenceParams.get(maxStep))){
                maxStep++;
            }
            if (maxStep == userActionSequenceParams.size()) break;
        }

        System.out.println("步骤匹配计算完成: 查询到的最大步骤号为: "+maxStep+",条件中的步骤数为: "+userActionSequenceParams.size());

        return maxStep;
    }
}
