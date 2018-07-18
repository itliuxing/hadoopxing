package com.xing.logAct;


import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.Test;

/**
 * @Class UserAgentParserTest
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/17 17:12
 * @Copyright Copyright by 兴兴
 * @Direction 类说明       测试useragent 信息
 */
public class UserAgentParserTest {

   // private String source = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36" ;
   //private String source = "Mozilla/5.0 (Linux; Android 7.1.1; vivo X20 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044112 Mobile Safari/537.36 MicroMessenger/6.6.7.1321(0x26060739) NetType/4G Language/zh_CN" ;
   private String source = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79 MicroMessenger/6.7.0 NetType/WIFI Language/zh_CN" ;

    public static void main(String[] args) {

    }

    /***
     * UserAgentParser 框架
     */
    @Test
    public void userAgentParserTest(){
        UserAgentParser userAgentParser  = new UserAgentParser();
        UserAgent agent = userAgentParser.parse(source);

        System.out.println( " Browser ： " +  agent.getBrowser() );
        System.out.println( " Engine ： " +  agent.getEngine() );
        System.out.println( " Engine version ： " +  agent.getEngineVersion() );
        System.out.println( " OS ： " +  agent.getOs() );
        System.out.println( " Platform ： " +  agent.getPlatform() );
        System.out.println( " isMobile ： " + agent.isMobile() ) ;
    }

    /***
     * yauaa 框架  解析出来的信息很惊人
     */
    @Test
    public void yauaaTest(){
        UserAgentAnalyzer uaa = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(25000)
                .build();
        nl.basjes.parse.useragent.UserAgent agent = uaa.parse( source );

        for (String fieldName: agent.getAvailableFieldNamesSorted()) {
            System.out.println(fieldName + " = " + agent.getValue(fieldName));
        }
    }


}
