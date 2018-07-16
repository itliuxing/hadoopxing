package com.xing.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Class 	ScoreAvgMapReduce.java
 * @Author 	作者姓名:刘兴
 * @Version	1.0
 * @Date	创建时间：2018-7-16 上午10:14:41
 * @Copyright Copyright by 兴兴
 * @Direction 类说明       使用MapReduce开发ScoreAvg 计算各科成绩平均成绩应用程序，本地做一次Reduce
 *
 * Hadoop处理命令：
 *      hadoop jar /usr/hadoop/data/hadoopxing-1.0-SNAPSHOT.jar com.xing.hadoop.mapreduce.CombinerApp hdfs://hadoop000:8020/hdfsapi/output/
 */
public class ScoreAvgMapReduce extends Configured implements Tool {


    public static class Avgmapping extends Mapper<Object,Text,Text,IntWritable>{
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String info = value.toString() ;

            StringTokenizer st = new StringTokenizer( value.toString() ) ;

            while ( st.hasMoreElements() ){
                String name = st.nextToken() ;
                String score = st.nextToken() ;
                context.write( new Text(name) ,new IntWritable( Integer.parseInt(score)) );
            }


        }
    }

    public static class AvgReduce extends Reducer<Text,IntWritable,Text,IntWritable>{
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0 ;
            int cishu = 0 ;
            for (IntWritable i : values ) {
                sum += i.get() ;
                cishu ++ ;
            }
            int avg = sum / cishu ;
            context.write( key ,new IntWritable( avg ) );
        }
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = new Configuration() ;

        //创建任务
        Job job = Job.getInstance( conf , "much file,avg math ." ) ;
        job.setJarByClass( ScoreAvgMapReduce.class  );

        //设置mapper处理以及Reduce处理
        job.setMapperClass( Avgmapping.class );
        job.setReducerClass( AvgReduce.class );

        //设置mapper ，reduce的输出参数类型
        job.setMapOutputValueClass( IntWritable.class );
        job.setMapOutputKeyClass( Text.class );

        job.setOutputValueClass( IntWritable.class );
        job.setOutputKeyClass( Text.class );

        //设置job的数据文件以及输出文件
        FileInputFormat.addInputPath( job, new Path( strings[0] ));
        FileOutputFormat.setOutputPath( job,new Path( strings[1] ) );

        //任务等待运行完成
        boolean isComplete = job.waitForCompletion( true ) ;
        return isComplete ? 0 : 1 ;
    }

    public static void main(String[] args) {
        //使用ToolRunner运行任务
        int result = 0 ;
        try {
            result = ToolRunner.run( new ScoreAvgMapReduce() ,
                    new String[]{"hdfs://hadoop000:8020/data/avgData.txt","hdfs://hadoop000:8020/dataout/avg/"} );
        } catch (Exception e) {
            System.out.println( "计算成绩平均数出现异常喽........." );
            e.printStackTrace();
        }
        System.exit( result );
    }

}
