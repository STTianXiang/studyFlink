package cn.doit.dynamice_rule.pojo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * @author not_today
 * @Date 2021/7/18 23:16
 * @Description:    用于封装日志属性,方便使用Json工具进行toJson
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogBean {

    private String account;
    private String appId;
    private String appVersion;
    private String carrier;
    private String deviceId;
    private String deviceType;
    private String ip;
    private Double latitude;
    private Double longitude;
    private String netType;
    private String osName;
    private String osVersion;
    private String releaseChannel;
    private String resolution;
    private String sessionId;
    private Long   timeStamp;
    private String eventId;
    private Map<String,String> properties;

}
