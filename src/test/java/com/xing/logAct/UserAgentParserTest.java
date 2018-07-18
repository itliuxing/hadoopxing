package com.xing.logAct;


import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.Test;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  /*  @Test
    public void userAgentParserTest(){
        UserAgentParser userAgentParser  = new UserAgentParser();
        UserAgent agent = userAgentParser.parse(source);

        System.out.println( " Browser ： " +  agent.getBrowser() );
        System.out.println( " Engine ： " +  agent.getEngine() );
        System.out.println( " Engine version ： " +  agent.getEngineVersion() );
        System.out.println( " OS ： " +  agent.getOs() );
        System.out.println( " Platform ： " +  agent.getPlatform() );
        System.out.println( " isMobile ： " + agent.isMobile() ) ;
    }*/

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

    private String text = "106.19.21.143 - - [18/Jul/2018:06:30:15 +0800] \"GET /servicesmng/oauth/wx07bf81a51358a68f/snsapi_base.form?code=081noraq1NoR0q0t9Eaq1ROdaq1noraZ&state=https%3A%2F%2Fwechat.zhdsbang.com%2Fweixin%2Findex.html%3Fouri%3D9ab7489f-0e61-4b6e-8fa2-2e6c9261e2bd%40supplydemand&appid=wx07bf81a51358a68f HTTP/1.1\" 302 - \"Mozilla/5.0 (Linux; Android 8.0; MHA-AL00 Build/HUAWEIMHA-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044109 Mobile Safari/537.36 MicroMessenger/6.6.7.1321(0x26060739) NetType/4G Language/zh_CN\" 360" ;

    /***
     * 测试提取数据
     */
    @Test
    public void parserSourceTest(){
        // 1：解析字符串，获取第三个引号至第四个引号之间的字符
        int start = StringUtils.getCharacterPosition( text , "\"" , 3 ) ;
        int end = StringUtils.getCharacterPosition( text , "\"" , 4 ) ;
        String userAgent = text.substring( start + 1 ,end ) ;
        System.out.println( " userAgent : " + userAgent );
        // 2：解析字符串，获取调用IP 0、请求时间 4、响应时间 最后
        StringTokenizer words = new StringTokenizer( text ) ;

        String requseIP = "";               //获取调用IP 0      106.19.21.143
        String requseTime = "" ;            //请求时间 3        [18/Jul/2018:06:30:15
        String responceTime = "" ;          //响应时间 最后       360

        String temp ;
        int addM = 0 ;
        while( words.hasMoreElements() ){
            temp = words.nextToken() ;
            if( addM == 0  ){
                requseIP = temp ;
            }
            if( addM== 3 ){
                requseTime = temp ;
            }
            if( ! words.hasMoreElements() ){
                responceTime = temp ;
            }
            addM ++ ;
        }
        System.out.println( " requseIP : " + requseIP  );
        System.out.println( " requseTime : " + requseTime  );
        System.out.println( " responceTime : " + responceTime  );
        // 3：解析userAgent
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
