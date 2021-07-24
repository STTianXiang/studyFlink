package cn.doit.dynamice_rule.datagen;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


/**
 *     用户画像数据模拟-> deviceid,K1=V1.
 *    因为画像默认存到Hbase中.Hbase是K_V类型..  我只需要把K1=V1 中的数字随机到1000,就能模拟用户的1000的画像特征.
 *
 *    但是首先得现在Hbase命令行客户端中创建好表.
 *
 *    > create 'test','f'
 */


public class UserProfileDataGen {
    public static void main(String[] args) throws IOException {

        Configuration conf = new Configuration();

        conf.set("hbase.zookeeper.quorum","tianxiang01:2181,tianxiang02:2181,tianxiang03:2181");

        Connection conn = ConnectionFactory.createConnection(conf);

        TableName table = TableName.valueOf("test");

        Table hbaseTable = conn.getTable(table);

        ArrayList<Put> puts = new ArrayList<>();

        for (int i = 1;i<=100000;i++) {  //生成10万个用户id

           for (int p = 1; p <= 50; p++) {  //每生成50条数据,就塞入一个List中.然后一起写入Hbase

                String deviceId = StringUtils.leftPad(i + "", 6, "0");

                Put put = new Put(Bytes.toBytes(deviceId));

                for (int j = 1; j <= 1000; j++) { //生成每个用户的1000个画像指标
                    String key = "tag" + j;
                    String value = "v" + RandomUtils.nextInt(1, 1000);
                    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(key), Bytes.toBytes(value));

                }
                //将这一条画像数据,添加到list中
                puts.add(put);
            }
           //积攒100条数据就提交一次
           if (puts.size()==100){
               hbaseTable.put(puts);
               puts.clear();
           }
        }
        conn.close();
    }
}
