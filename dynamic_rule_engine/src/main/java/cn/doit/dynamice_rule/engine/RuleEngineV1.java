package cn.doit.dynamice_rule.engine;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.ResultBean;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * @author not_today
 * @Date 2021/7/24 11:34
 * @Description
 *     测试规则:
 *          如果用户行为日志触发了 E事件 ( eventId = E )
 *          行为日志中且做过 U事件(且 p1=v3,p2=v2) >=3次 并且 G(p6=v8,p4=v5,p1=v2)>=1
 *          行为次序,行为日志中依次做过 W(p1=v4) -> R(p2=v3) -> F()
 *          画像属性中满足 k3=v3 ; k100=v80 ; k230=v360
 */
public class RuleEngineV1 {
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //添加一个Kafka数据源source
        Properties props = new Properties();
        props.setProperty("bootstrap.servers","tianxiang01:9092,tianxiang02:9092,tianxiang03:9092");//kafka地址
        props.setProperty("auto.offset.reset","latest"); //从那里开始消费,消费最新的
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), props);

        //将数据加入env中
        DataStreamSource<String> logStreaming = env.addSource(kafkaConsumer);

        //测试是否正常连接kafka并消费数据
        //logStreaming.print();
        //env.execute("RuleEngineV1");

        //先将Kafka中的Json串转为LogBean
        SingleOutputStreamOperator<LogBean> beanStream = logStreaming.map(new MapFunction<String, LogBean>() {
            @Override
            public LogBean map(String value) throws Exception {
                return JSON.parseObject(value, LogBean.class);
            }
        });


        //提升并行度.按照每个设备id进行分组.就可以每组一个Task
        KeyedStream<LogBean, String> keyStream = beanStream.keyBy(new KeySelector<LogBean, String>() {
            @Override
            public String getKey(LogBean value) throws Exception {
                return value.getDeviceId();
            }
        });

        //然后在这个KeyStream中对每个Key进行操作
        SingleOutputStreamOperator<ResultBean> resultStream = keyStream.process(new KeyedProcessFunction<String, LogBean, ResultBean>() {
            //上面三个泛型分别代表:分key时,key的类型;数据本身类型;返回结果(那个人满足那个规则)->因为我要返回一堆结果,所以我写一个Bean来封装结果

            Connection conn;
            TableName table;
            Table hbaseTable;
            ListStateDescriptor<LogBean> events_state;
            ListState<LogBean> eventsState;

            @Override
            public void open(Configuration parameters) throws Exception {
                org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.set("hbase.zookeeper.quorum","tianxiang01:2181,tianxiang02:2181,tianxiang03:2181");
                conn = ConnectionFactory.createConnection(conf);
                table = TableName.valueOf("test");
                hbaseTable = conn.getTable(table);

                //定义一个List结构的State 传入的参数,首先是state的名字,然后如果是简单类型就直接 .class
                events_state = new ListStateDescriptor<LogBean>("events_state", LogBean.class);// 泛型中需要指定你要存储什么东西,这里我需要存储下面的LogBean
                eventsState = getRuntimeContext().getListState(events_state);//这里面要求传入一个描述这个liststate的对象-> ListStateDescriptor -> 讲白了就是给这个listState取个名字,只是这个名字要封装到一个对象里面
            }

            @Override
            public void processElement(LogBean value, Context ctx, Collector<ResultBean> out) throws Exception {
                //将数据存入State中攒起来
                eventsState.add(value);


                //首先判断用户行为是否满足触发条件: E事件 ( eventId = E )
                if ("E".equals(value.getEventId())){
                    //判断 画像属性中满足 k3=v3 ; k100=v80 ; k230=v360  画像数据是在Hbase中.
                    /* 查询Hbase 可以直接 table.get() 但是它要求传入一个 Get对象. 这个对象封装了你的查询条件
                                我的查询条件是 取出 k3 , k100 , k230  这三列 并判断是否等于 v3 , v80 , v360 */
                    Get get = new Get(Bytes.toBytes(value.getDeviceId()));//这就拿出一行数据. deviceid(设备id)->用户id
                    /* 但是我没必要整行拿出来.我只需要拿 k3,k100,k230 这三列,看下是否满足条件 */
                    get.addColumn(Bytes.toBytes("f"),Bytes.toBytes("k3"));//addColumn 需要传入Hbase的Family和Qualifier
                    get.addColumn(Bytes.toBytes("f"),Bytes.toBytes("k100"));
                    get.addColumn(Bytes.toBytes("f"),Bytes.toBytes("k230"));

                    //传入查询条件并查询
                    Result result = hbaseTable.get(get);//这里得到的result结果其实都是 K_V 类型的数据
                    /** 直接取出来的是 Byts数组.. 如果要拿来比较需要转换成String类型
                       byte[] k3 = result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k3")); */
                    //为什么不用toString呢? (toString默认返回的是对象的内存地址即hashCode,而new String返回的是真实的值)
                    /**  String k3_value = result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k3")).toString();*/
                    String k3   = new String(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k3")));
                    String k100 = new String(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k100")));
                    String k230 = new String(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k230")));

                    if ("v3".equals(k3) && "v80".equals(k100) && "v360".equals(k230)){
                        //如果都满足画像属性就开始查询行为属性次数条件 做过 U事件(且 p1=v3,p2=v2) >=3次 并且 G(p6=v8,p4=v5,p1=v2)>=1
                        /**
                         这里需要查询用户 U事件是否做过3次, G事件是否做过1次. 这里我们并没有一个外部存储事件明细
                         意味着 当我们的 processElement 接收到一个LogBean的时候我们就攒起来了.
                         用什么结构存储呢? Map?(我不需要getvalue等等)
                         因为一开始我已经对这个流进行了  keyBy.. 所以我的一个存储对应了一个 key.
                         我的key是 deviceId 等价于 唯一会员账号,所以我是一个人一个存储..
                         所以用list结构   一开始拿到这个 logbean 的时候就先存入state攒起来
                           先在最开始的时候就把数据先攒起来
                         */
                        Iterable<LogBean> logBeans = eventsState.get();//state并不能直接使用,要先get.得到一个可迭代的.
                        /* 要判断事件次数以及事件中的属性条件是否满足 先定义两个计数器 */
                        int u_count =0;
                        int g_count =0;

                        for (LogBean bean : logBeans) {
                            if ("U".equals(bean.getEventId())){
                                Map<String, String> Upro = bean.getProperties();
                                String p1 = Upro.get("p1");
                                String p2 = Upro.get("p2");
                                if ("v3".equals(p1) && "v2".equals(p2)) u_count++;
                            }

                            if ("G".equals(bean.getEventId())){
                                Map<String, String> Gpro = bean.getProperties();
                                String p6 = Gpro.get("p6");
                                String p4 = Gpro.get("p4");
                                String p1 = Gpro.get("p1");
                                if ("v8".equals(p6) && "v5".equals(p4) && "v2".equals(p1)) g_count++;
                            }
                        }

                        if (u_count>=3 && g_count>=1){
                            //如果用户行为次数满足条件..那么继续判断用户行为次序条件是否满足
                            //判断 行为次序,行为日志中依次做过 W(p1=v4) -> R(p2=v3) -> F()

                            ArrayList<LogBean> beanList = new ArrayList<>();
                            CollectionUtils.addAll(beanList,logBeans.iterator());
                            //要判断顺序需要遍历 放进List里面进行遍历比较简单
                            // R W R W R R W W F    ->假设顺序是这样的
                            int index = -1;
                            for (int i=0;i<beanList.size();i++){
                                LogBean beani = beanList.get(i);
                                if ("W".equals(beani.getEventId())){
                                    Map<String, String> properties = beani.getProperties();
                                    String p1 = properties.get("p1");
                                    if ("v4".equals(p1)){
                                        index = i;
                                        break;
                                    }
                                }
                            }
                            /*
                            先找到 W事件(属性 p1=v4) 然后把角标赋值给 index
                                                   */

                            //然后从index角标后面又开始找 R事件(属性 p2=v3 )
                            int index2 = -1;
                            if (index >= 0 && index+1< beanList.size()){

                                for (int i=index+1;i<beanList.size();i++){
                                    LogBean beani = beanList.get(i);
                                    if ("R".equals(beani.getEventId())){
                                        Map<String, String> properties = beani.getProperties();
                                        String p2 = properties.get("p2");
                                        if ("v3".equals(p2)){
                                            index2 = i;
                                            break;
                                        }
                                    }
                                }
                            }

                            //上面找到了 R事件后,看下 index2角标后面的事件是否有F.
                            int index3 = -1;
                            if (index2>=0 && index2+1<beanList.size()){
                                for (int i= index2+1; i< beanList.size();i++){
                                    LogBean beani = beanList.get(i);
                                    if ("F".equals(beani.getEventId())){
                                        index3=i;
                                        break;
                                    }
                                }
                            }

                            //如果 index3 大于 -1 说明 W -> R -> F 事件依次找到了
                            if (index3>-1){
                                ResultBean resultBean = new ResultBean();

                                resultBean.setDeviceId(value.getDeviceId());
                                resultBean.setRuleId("test_rule_version_1");
                                resultBean.setTimeStamp(value.getTimeStamp());

                                out.collect(resultBean);
                            }
                        }
                    }
                }
            }
        });
        resultStream.print();
        env.execute("RuleEngineV1");
    }
}
