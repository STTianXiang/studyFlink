package cn.doit.dynamice_rule.functions;

import cn.doit.dynamice_rule.pojo.LogBean;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * @author not_today
 * @Date 2021/8/8 12:29
 * @Description
 */
public class DeviceKeySelector implements KeySelector<LogBean,String> {
    @Override
    public String getKey(LogBean value) throws Exception {
        return value.getDeviceId();
    }
}
