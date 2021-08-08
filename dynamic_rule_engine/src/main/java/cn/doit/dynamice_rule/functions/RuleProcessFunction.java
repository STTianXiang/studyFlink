package cn.doit.dynamice_rule.functions;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.ResultBean;
import cn.doit.dynamice_rule.pojo.RuleParam;
import cn.doit.dynamice_rule.service.*;
import cn.doit.dynamice_rule.tools.RuleSimulator;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @author not_today
 * @Date 2021/8/8 12:32
 * @Description
 */

// 因为 KeyedProcessFunction 是由 abstract 修饰的 抽象类. 不能被实现,只能继承
public class RuleProcessFunction extends KeyedProcessFunction<String, LogBean, ResultBean> {
    /** 因为这些查询 已经写了查询服务了.所以我这里只需要调service的对象 */
    private UserProfileQueryService userProfileQueryService;
    private UserActionCountQueryService userActionCountQueryService;
    private UserActionSequenceQueryService userActionSequenceQueryService;

    RuleParam ruleParam;

    ListState<LogBean> eventState;
    ListStateDescriptor<LogBean> state;


    @Override
    public void open(Configuration parameters) throws Exception {

        userProfileQueryService = new UserProfileQueryServiceHbaseImpl();
        userActionCountQueryService = new UserActionCountQueryServiceStateImpl();
        userActionSequenceQueryService = new UserActionSequenceQueryServiceStateImpl();

        ruleParam = RuleSimulator.getRuleParam();

        state = new ListStateDescriptor<>("eventState", LogBean.class);
        eventState = getRuntimeContext().getListState(state);
    }

    @Override
    public void processElement(LogBean logbean, Context ctx, Collector<ResultBean> out) throws Exception {
        //将接收到的数据放入历史的State中
        eventState.add(logbean);


        //判断是否满足触发条件
        if (ruleParam.getTriggerParam().getEventId().equals(logbean.getEventId())){

            //查询用户画像条件
            boolean userProMatch = userProfileQueryService.judgeProfileCondition(logbean.getDeviceId(), ruleParam);
            if (!userProMatch) return;

            //判断是否满足次数条件
            boolean countsMatch = userActionCountQueryService.queryActionCounts(eventState, ruleParam);
            if (!countsMatch) return;

            //判断是否满足次序条件
            boolean sequenceMatch = userActionSequenceQueryService.queryActionSequence(eventState, ruleParam);
            if (!sequenceMatch) return;


            //返回满足的ResultBean
            ResultBean resultBean = new ResultBean();
            resultBean.setTimeStamp(logbean.getTimeStamp());
            resultBean.setRuleId(ruleParam.getRuleId());
            resultBean.setDeviceId(logbean.getDeviceId());


            out.collect(resultBean);
        }
    }
}
