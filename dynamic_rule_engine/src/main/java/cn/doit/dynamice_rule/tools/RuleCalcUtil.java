package cn.doit.dynamice_rule.tools;

import cn.doit.dynamice_rule.pojo.LogBean;
import cn.doit.dynamice_rule.pojo.RuleAtomicParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author not_today
 * @Date 2021/8/4 22:38
 * @Description   规则计算中可能用到的各类工具方法
 */
public class RuleCalcUtil {

    public static boolean eventBeanMatchActionParam(LogBean eventBean, RuleAtomicParam eventParam){
        //参数对象 和 事件对象 是否符合条件
        if (eventBean.getEventId().equals(eventParam.getEventId())){
            //如果传入的一个事件的事件id与参数中的事件id相同,就进行属性判断
            HashMap<String, String> paramProperties = eventParam.getProperties();//取出条件中的事件属性
            Set<Map.Entry<String, String>> entries = paramProperties.entrySet();

            Map<String, String> eventProperties = eventBean.getProperties();//取出等待进行判断事件中的属性

            for (Map.Entry<String, String> entry : entries) { //遍历条件中的每个属性值
                if (!entry.getValue().equals(eventProperties.get(entry.getKey()))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}
