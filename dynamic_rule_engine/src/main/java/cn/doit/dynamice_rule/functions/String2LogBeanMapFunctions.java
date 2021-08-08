package cn.doit.dynamice_rule.functions;

import cn.doit.dynamice_rule.pojo.LogBean;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author not_today
 * @Date 2021/8/8 12:14
 * @Description
 */
public class String2LogBeanMapFunctions implements MapFunction<String, LogBean> {
    @Override
    public LogBean map(String value) throws Exception {

        return JSON.parseObject(value,LogBean.class);
    }
}
