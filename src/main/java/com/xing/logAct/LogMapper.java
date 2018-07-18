package com.xing.logAct;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Class LogMapper
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/18 10:40
 * @Copyright Copyright by 兴兴
 * @Direction 类说明       map处理类
 */
public class LogMapper extends Mapper<LongWritable,Text,Text ,LongWritable> {

    // userAgent处理
    protected UserAgentAnalyzer uaa = null ;

    /**
     * 初始化调用,只初始化一次
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        uaa = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(25000)
                .build();
    }

    /**
     * 销毁调用
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        uaa = null ;
    }

    /**
     * Map 处理数据
     * @param key
     * @param value
     * @param context
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String info = value.toString() ;
        // 1：解析字符串，获取调用IP 0、请求时间 4、响应时间 最后
        StringTokenizer words = new StringTokenizer( info ) ;
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
        //System.out.println( " requseIP : " + requseIP  );
        //System.out.println( " requseTime : " + requseTime  );
        //System.out.println( " responceTime : " + responceTime  );
        // 1.1：将响应的时间返回  需求：1
        context.write( new Text( responceTime ) , new LongWritable( 1 ) );

        // 2：解析字符串，获取第三个引号至第四个引号之间的字符
        int start = StringUtils.getCharacterPosition( info , "\"" , 3 ) ;
        int end = StringUtils.getCharacterPosition( info , "\"" , 4 ) ;
        String userAgentSource = info.substring( start + 1 ,end ) ;
        //System.out.println( " userAgent : " + userAgentSource );

        // 3：解析userAgent
        nl.basjes.parse.useragent.UserAgent agent = uaa.parse( userAgentSource );
        for (String fieldName: agent.getAvailableFieldNamesSorted()) {
            //System.out.println(fieldName + " = " + agent.getValue(fieldName));
            if( fieldName.equals("OperatingSystemName") ){
                // 3.1：获取操作系统  对应需求 3
                context.write( new Text( agent.getValue(fieldName) ) , new LongWritable( 1 ) );
            }
            if( fieldName.equals("AgentNameVersion") ){
                // 3.1：获取微信版本  对应需求 2
                context.write( new Text( agent.getValue(fieldName) ) , new LongWritable( 1 ) );
            }
        }
    }


}
