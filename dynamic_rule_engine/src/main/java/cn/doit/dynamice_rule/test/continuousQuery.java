package cn.doit.dynamice_rule.test;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author not_today
 * @Date 2021/8/7 15:39
 * @Description   面试问题: 查找连续7天登录的用户
 */
public class continuousQuery {
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        //创建Socket数据源
        DataStream<String> dataSource = env.socketTextStream("192.1.30.30", 8888);






    }
}
