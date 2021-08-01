package cn.doit.dynamice_rule.moduletest;

import cn.doit.dynamice_rule.pojo.RuleParam;
import cn.doit.dynamice_rule.service.UserProfileQueryServiceHbaseImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author not_today
 * @Date 2021/8/1 18:59
 * @Description
 */
public class ProfileQueryTest {
    /** version 1.1 -> 画像条件查询服务模块测试类 */
    @Test
    public void testQueryProfile() throws IOException {
        //为了避免测试还要启动flink,所以自己构造数据
        HashMap<String, String> userProfileParams = new HashMap<>();
        userProfileParams.put("tag1","v88");
        userProfileParams.put("tag2","v38");

        RuleParam ruleParam = new RuleParam();
        ruleParam.setUserProfileParams(userProfileParams);

        //这里抛出IO异常.这里可以直接抛出.
        UserProfileQueryServiceHbaseImpl impl = new UserProfileQueryServiceHbaseImpl();
        boolean result = impl.judgeProfileCondition("000645", ruleParam);
        System.out.println(result);

    }
}
