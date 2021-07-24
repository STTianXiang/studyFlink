package cn.doit.dynamice_rule.benchmark;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @author not_today
 * @Date 2021/7/24 22:06
 * @Description   Hbase  1000次请求压力测试
 */
public class hbaseGetTest {
    public static void main(String[] args) throws IOException {

        Configuration conf = new Configuration();

        conf.set("hbase.zookeeper.quorum","tianxiang01:2181,tianxiang02:2181,tianxiang03:2181");

        Connection conn = ConnectionFactory.createConnection(conf);

        TableName table = TableName.valueOf("test");

        Table hbaseTable = conn.getTable(table);

        //计时开始
        long start = System.currentTimeMillis();
        for (int i=0;i<1000;i++){

            Get get = new Get(StringUtils.leftPad(RandomUtils.nextInt(1, 900000) + "", 6, "0").getBytes()); //这里get的是整行数据..一般不会要整行,只会要几列

            int i1 = RandomUtils.nextInt(1, 100);
            int i2 = RandomUtils.nextInt(1, 100);
            int i3 = RandomUtils.nextInt(1, 100);

            get.addColumn("f".getBytes(), Bytes.toBytes("tag"+RandomUtils.nextInt(1,100)));
            get.addColumn("f".getBytes(), Bytes.toBytes("tag"+RandomUtils.nextInt(1,100)));
            get.addColumn("f".getBytes(), Bytes.toBytes("tag"+RandomUtils.nextInt(1,100)));

            Result result = hbaseTable.get(get);

            byte[] v1_value = result.getValue("f".getBytes(), Bytes.toBytes("tag" + i1));
            byte[] v2_value = result.getValue("f".getBytes(), Bytes.toBytes("tag" + i2));
            byte[] v3_value = result.getValue("f".getBytes(), Bytes.toBytes("tag" + i3));

        }
        long end = System.currentTimeMillis();

        System.out.println(start-end);
        conn.close();

    }
}
