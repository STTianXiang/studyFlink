package cn.doit.dynamice_rule.datagen;

/**
 * @author not_today
 * @Date 2021/7/18 22:21
 * @Description:    数据模拟器->行为日志生成
 */

import cn.doit.dynamice_rule.pojo.LogBean;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Properties;

/**     日志格式如下.
 {
    "account": "Vz54E9Ya",
    "appId": "cn.doitedu.app1",
    "appVersion": "3.4",
    "carrier": "中国移动",
    "deviceId": "WEISLD0235S0934OL",
    "deviceType": "MI-6",
    "ip": "24.93.136.175",
    "latitude": 42.09287620431088,
    "longitude": 79.42106825764643,
    "netType": "WIFI",
    "osName": "android",
    "osVersion": "6.5",
    "releaseChannel": "豌豆荚",
    "resolution": "1024*768",
    "sessionId": "SE18329583458",
    "timeStamp": 1594534406220
    "eventId": "productView",
    "properties": {
         "pageId": "646",
         "productId": "157",
         "refType": "4",
         "refUrl": "805",
         "title": "爱得堡 男靴中高帮马丁靴秋冬雪地靴 H1878 复古黄 40 码",
         "url": "https://item.jd.com/36506691363.html",
         "utm_campain": "4",
         "utm_loctype": "1",
         "utm_source": "10"
       }
 }

 */
public class ActionLogGen {
    public static void main(String[] args) throws InterruptedException {
        /*  首先日志的格式是一个Json.只需要封装成一个对象.然后用一个Json工具 toJson就可以了
            所以我生成一个LogBean来封装
         */

        // Runnable接口中并没有 open方法. 所以并不能在open中实现运行且只运行一次. 所以把kafka配置提到最外面来
            /**  但是这10个线程都用我一个Producer,而Kafka是没有保障线程安全
             *        所以我需要把 new KafkaProducer 放到for循环线程中,while循环外.. 这样就会生成10个Producer */
        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers","tianxiang01:9092,tianxiang02:9092,tianxiang03:9092");
        pro.put("acks","all");// acks=0 配置适用于实现非常高的吞吐量, acks=all 这是最安全模式
        pro.put("batch.size",1024);//发送到同一个partition的消息会被先存储在batch中,该参数指定一个batch可以使用的内存大小,单位是byte,不一定需要等到batch满了才发送. 默认是 16384=16KB
        pro.put("linger.ms",1);//生产者在发送消息前等待linger.ms.从而等待更多的消息加入batch中,如果batch被填满就或者linger.ms达到上限,就把batch中的消息发送出去.当linger.ms大于0时,延时性会增加,但是会提高吞吐量,因为会减少消息发送频率
        pro.put("buffer.memory",33554432);//32MB.
        pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        pro.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");


        /** Version 3.0  但是在2.0中还是一个线程在工作. 所以我写一个for循环.让10个线程工作 */
        for (int i=0;i<10;i++){
            /** Version 2.0  一条条写太慢.创建一个线程写 然后实现接口方法,把while循环放进方法中 */
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Properties pro = new Properties();
                    pro.setProperty("bootstrap.servers","tianxiang01:9092,tianxiang02:9092,tianxiang03:9092");
                    pro.put("acks","all");// acks=0 配置适用于实现非常高的吞吐量, acks=all 这是最安全模式
                    pro.put("batch.size",1024);//发送到同一个partition的消息会被先存储在batch中,该参数指定一个batch可以使用的内存大小,单位是byte,不一定需要等到batch满了才发送. 默认是 16384=16KB
                    pro.put("linger.ms",1);//生产者在发送消息前等待linger.ms.从而等待更多的消息加入batch中,如果batch被填满就或者linger.ms达到上限,就把batch中的消息发送出去.当linger.ms大于0时,延时性会增加,但是会提高吞吐量,因为会减少消息发送频率
                    pro.put("buffer.memory",33554432);//32MB.
                    pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
                    pro.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

                    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(pro);

                    while (true){
                        LogBean logBean = new LogBean();

                        String account = StringUtils.leftPad(RandomUtils.nextInt(1, 10000) + "", 6, "0");//RandomUtils.nextInt需要传入上限和下限,leftPad:左边补齐,总共6位,不足的补上0  ->账号
                        logBean.setAccount(account);
                        /* AppId 就写死 */
                        logBean.setAppId("Study");
                        logBean.setAppVersion("1.0");
                        logBean.setCarrier("电信");//运营商
                        logBean.setDeviceId(account);//设备Id
                        logBean.setIp("192.1.30.30");
                        logBean.setLatitude(RandomUtils.nextDouble(10.0,52.0));//维度
                        logBean.setLongitude(RandomUtils.nextDouble(120.0,160.0));//经度
                        logBean.setDeviceType("vivo");//设备类型
                        logBean.setNetType("5G");//网络类型
                        logBean.setOsName("IOS");//OS名称
                        logBean.setOsVersion("10.6");//OS版本
                        logBean.setReleaseChannel("APPStroe");//发布渠道
                        logBean.setResolution("2048*1024");//分辨率
                        logBean.setEventId(RandomStringUtils.randomAlphabetic(1));//事件类型,randomAlphabetic:随机生成一个字母


                        HashMap<String, String> properties = new HashMap<String, String>();
                        //往properties中塞5个属性.
                        for (int i=0;i<RandomUtils.nextInt(1,5);i++){
                            //属性分别是 key-> p1-p10 , value-> v1-v10.
                            properties.put("p"+RandomUtils.nextInt(1,10),"v"+RandomUtils.nextInt(1,10));
                        }
                        logBean.setProperties(properties);

                        logBean.setTimeStamp(System.currentTimeMillis());
                        logBean.setSessionId(RandomStringUtils.randomNumeric(10,10));//产生一个10位数字的字符串作为会话id

                        String log = JSON.toJSONString(logBean);


                        /**  Version 1.0  写在 while循环中,每拼接一条数据就生成一个KafkaProducer 这样非常不友好    */
            /*Properties pro = new Properties();
            pro.setProperty("bootstrap.servers","tianxiang01:9092,tianxiang02:9092,tianxiang03:9092");
            pro.put("acks","all");// acks=0 配置适用于实现非常高的吞吐量, acks=all 这是最安全模式
            pro.put("batch.size",1024);//发送到同一个partition的消息会被先存储在batch中,该参数指定一个batch可以使用的内存大小,单位是byte,不一定需要等到batch满了才发送. 默认是 16384=16KB
            pro.put("linger.ms",1);//生产者在发送消息前等待linger.ms.从而等待更多的消息加入batch中,如果batch被填满就或者linger.ms达到上限,就把batch中的消息发送出去.当linger.ms大于0时,延时性会增加,但是会提高吞吐量,因为会减少消息发送频率
            pro.put("buffer.memory",33554432);//32MB.
            pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
            pro.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(pro);

            ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("test", log);
            kafkaProducer.send(producerRecord);*/

                        //验证数据是否正确.
                        System.out.println(log);

                        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("test", log);
                        kafkaProducer.send(producerRecord);

                        /* sleep是有异常的.一开始我选择抛出到main方法中. 但是现在是在 实现接口的run方法中.run方法是不允许抛出异常的 */
                        try {
                            Thread.sleep(RandomUtils.nextInt(500,3000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        }
    }
}
