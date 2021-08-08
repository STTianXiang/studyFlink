package cn.doit.dynamice_rule.test;

/**
 * @author not_today
 * @Date 2021/8/7 13:31
 * @Description
 */
public class isEmptyFunctionTest {

    public static void main(String[] args) {

        String a = "";

        boolean empty1 = a.isEmpty();

        String b = null;

        boolean empty2 = b == null;

        //boolean empty2 = b.isEmpty();

        System.out.println(empty1);
        //System.out.println(empty2);


        /**
         *   结论: isEmpty() 用于判断内容是否为空,必须是在本身不是空的引用情况下才行.
         *      一般情况下, null和 isEmpty 是联合使用
         */
        if ( a != null && !a.isEmpty()){
            System.out.println("test");
        }

    }
}
