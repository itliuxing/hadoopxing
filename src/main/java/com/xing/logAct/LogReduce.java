package com.xing.logAct;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Class LogReduce
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/18 11:05
 * @Copyright Copyright by 兴兴
 * @Direction 类说明      log,userAgent  reduce处理
 */
public class LogReduce extends Reducer<Text,LongWritable,Text,LongWritable> {

    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        // 1.0：将Reduce计算出来的结果再写入到上下文，供输出时使用
        Long sum = (long)0 ;
        for (LongWritable value : values ) {
            sum += value.get() ;
        }
        // 1.1：将Reduce计算出来的结果再写入到上下文，供输出时使用
        context.write(key, new LongWritable(sum) );
    }
}
