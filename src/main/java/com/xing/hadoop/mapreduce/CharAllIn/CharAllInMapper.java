package com.xing.hadoop.mapreduce.CharAllIn;

import org.apache.hadoop.mapreduce.Mapper;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Class CharAllInMapper
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/16 11:53
 * @Copyright Copyright by 兴兴
 * @Direction 类说明
 */
public class CharAllInMapper extends Mapper<Object ,Text ,Text ,Text > {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        //每行数据解析
        String keys = key.toString() ;
        StringTokenizer stToken = new StringTokenizer( keys ) ;

        //stToken.
    }
}
