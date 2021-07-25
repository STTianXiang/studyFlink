package cn.doit.dynamice_rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author not_today
 * @Date 2021/7/25 13:34
 * @Description
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RuleAtomicParam implements Serializable {
    //规则参数中的原子条件封装实体

    private String eventId;  //事件的类型要求
    private HashMap<String,String> properties; //事件的属性要求
    private int cnts;  //事件的阈值要求
    private long rangeStart; //要求事件发生时间段起始
    private long rangeEnd; //要求事件发生时间段结束

}
