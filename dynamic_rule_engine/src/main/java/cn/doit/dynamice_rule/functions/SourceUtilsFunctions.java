package cn.doit.dynamice_rule.functions;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;


import java.util.Properties;

/**
 * @author not_today
 * @Date 2021/8/7 13:22
 * @Description
 */
public class SourceUtilsFunctions {

    //懒得new  设置为Static的..
    public static FlinkKafkaConsumer<String> getKafkaSource(String topicName){

        Properties pro = new Properties();
            pro.setProperty("bootstrap.servers","tianxiang01:9092,tianxiang02:9092,tianxiang03:9092");
            pro.put("acks","all");// acks=0 配置适用于实现非常高的吞吐量, acks=all 这是最安全模式
            pro.put("batch.size",1024);//发送到同一个partition的消息会被先存储在batch中,该参数指定一个batch可以使用的内存大小,单位是byte,不一定需要等到batch满了才发送. 默认是 16384=16KB
            pro.put("linger.ms",1);//生产者在发送消息前等待linger.ms.从而等待更多的消息加入batch中,如果batch被填满就或者linger.ms达到上限,就把batch中的消息发送出去.当linger.ms大于0时,延时性会增加,但是会提高吞吐量,因为会减少消息发送频率
            pro.put("buffer.memory",33554432);//32MB.
            pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
            pro.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");



            if ( !topicName.isEmpty() && topicName != null){
                FlinkKafkaConsumer<String> source = new FlinkKafkaConsumer<>(topicName, new SimpleStringSchema(),pro);
                return source;
            }
            throw new RuntimeException("请输入正确的Topic名称.");
    }
}
