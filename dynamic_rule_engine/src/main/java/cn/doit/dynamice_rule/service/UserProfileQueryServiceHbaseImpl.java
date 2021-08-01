package cn.doit.dynamice_rule.service;

import cn.doit.dynamice_rule.pojo.RuleParam;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author not_today
 * @Date 2021/7/27 22:20
 * @Description
 *      UserProfileQueryService 接口的实现类.. 用于查询Hbase中的画像数据..
 */
// TODO: 2021/8/1 version 1.1 -> 此次版本只返回了查询结果是否成立,而查询到的具体值并没有返回.后续版本可以考虑返回后让业务方缓存起来,避免无谓的查询
public class UserProfileQueryServiceHbaseImpl implements UserProfileQueryService {

    //你给我什么. 我给你什么  -> 这一块只需要实现这个逻辑
       //这里需要你给我整个规则参数.我从这里面拿到画像的几个条件,以及deviceId  而这个实现类会告诉你是否满足条件


    //先在构造函数中创建Hbase连接
    /** 为什么要在构造函数中创建连接呢?
     因为:    Java的构造函数也叫构造方法,是一种特殊的函数.无返回值.
        一般是用来初始化成员属性和成员方法的,即 new 对象产生后,就调用了对象的属性和方法.
        比如说:人一出生就有了年龄、体重、身高、就会哭.我们可以将这些天然的属性和行为定义在构造函数中,
     当new实例化对象时,就具有这些属性和方法了,就没必要再去重新定义,从而加快变成效率
        构造函数是对象一旦建立就给对象初始化,包括属性,执行方法中的语句.
        而一般函数是对象调用才执行,用  " .方法名 "的方式给对象添加功能
     一个对象建立,构造函数只运行一次,而一般函数可以被该对象调用多次
       构造函数在new对象的时候就会调用  例子如下:  */
    /*
    public class ConfunDemo {
        public static void main(String[] args) {
            Confun c1=new Confun();//输出Hello World。new对象一建立，就会调用对应的构造函数Confun()，并执行其中的println语句。
        }
    }
    class Confun{
        Confun(){        //定义构造函数，输出Hello World
            System.out.println("Hellow World");
        }
    }*/
    Connection conn;
    TableName table;
    Table hbaseTable;
    public UserProfileQueryServiceHbaseImpl() throws IOException {
        Configuration conf = new Configuration();

        conf.set("hbase.zookeeper.quorum","tianxiang01:2181,tianxiang02:2181,tianxiang03:2181");

        conn = ConnectionFactory.createConnection(conf);

        table= TableName.valueOf("test");

        hbaseTable = conn.getTable(table);
    }

    @Override
    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam) {
        //传入一个用户号,以及条件.返回是否满足

        //因为我这里传入的是整体参数,所以先把画像参数拿出来(Key就是想要查询的标签名)
        HashMap<String, String> userProfileParams = ruleParam.getUserProfileParams();

        Set<String> tagNames = userProfileParams.keySet();//取出条件中所要求的等待查询标签名

        //构造Hbase查询条件参数
        Get get = new Get(deviceId.getBytes());
        //把要查询的标签(Hbase中的列)添加查询条件中.
        for (String tagName : tagNames) {
            get.addColumn("f".getBytes(),tagName.getBytes());
        }

        //调用Hbase的查询API,进行查询
        try {/** 这里需要抛出一个IO异常,但是完全没有必要抛出,因为这里抛出的话,会抛到接口中..但是
         如果我本来就没查询到任何信息,或者Hbase挂掉了,可以叫运维.没必要整个程序停掉..*/
            Result result = hbaseTable.get(get);
            //判断查询的结果和要求是否一致
            for (String tagName : tagNames) {
                byte[] valueBytes = result.getValue("f".getBytes(), tagName.getBytes());
                //判断查询到的value和条件要求的value是否一致.如果不一致,方法直接返回false
                if (!(valueBytes!=null && new String(valueBytes).equals(userProfileParams.get(tagName)))){
                    return false;
                }
            }
          //如果走到这里,说明每个标签的查询值都等于条件要求中的值,可以返回false
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果查询出现异常,默认返回false;
        return false;
    }

}
