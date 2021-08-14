package cn.doit.dynamice_rule.engine;

import cn.doit.dynamice_rule.functions.DeviceKeySelector;
import cn.doit.dynamice_rule.functions.RuleProcessFunction;
import cn.doit.dynamice_rule.functions.SourceUtilsFunctions;
import cn.doit.dynamice_rule.functions.String2LogBeanMapFunctions;
import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.ResultBean;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * @author not_today
 * @Date 2021/8/7 13:18
 * @Description  静态规则引擎version2.0主程序
 */
public class RuleEngineV2 {
    public static void main(String[] args) throws Exception {

        //开启Flink本地界面模式  默认是 主机名(IP):8081
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        DataStreamSource<String> logStream = null;

        try {
            //添加一个Source
            logStream = env.addSource(SourceUtilsFunctions.getKafkaSource("test"));
        } catch (Exception e) {
            e.printStackTrace();
            TimeUnit.SECONDS.sleep(10);
            throw new RuntimeException("未输入正确的Topic名称,请重试!");
        }

        //将Json字符串转为LogBean
        SingleOutputStreamOperator<LogBean> logbean = logStream.map(new String2LogBeanMapFunctions());

        //根据key进行分组
        KeyedStream<LogBean, String> keyed = logbean.keyBy(new DeviceKeySelector());

        //核心判断处理
        SingleOutputStreamOperator<ResultBean> resultBean = keyed.process(new RuleProcessFunction());

        //打印展示
        resultBean.print();

        env.execute("RuleEngineV2");
    }
}
