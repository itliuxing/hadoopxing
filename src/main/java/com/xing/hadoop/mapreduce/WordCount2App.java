package com.xing.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @Class 	WordCountApp.java
 * @Author 	作者姓名:刘兴
 * @Version	1.0
 * @Date	创建时间：2018-7-14 下午6:24:41
 * @Copyright Copyright by 兴兴
 * @Direction 类说明       使用MapReduce开发wordcount应用程序,处理了第二次进入时出现异常的处理
 *
 * Hadoop处理命令：
 *      hadoop jar /usr/hadoop/data/hadoopxing-1.0-SNAPSHOT.jar com.xing.hadoop.mapreduce.WordCount2App hdfs://hadoop000:8020/hdfsapi/hello.txt hdfs://hadoop000:8020/hdfsapi/output/wc2
 */

public class WordCount2App {

    /****
     * 每行字符的分割点几点计算
     */
    public static class MyMapper extends Mapper<LongWritable,Text,Text ,LongWritable>{

        LongWritable one = new LongWritable(1) ;

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 1：第一步分割传入的行数据
            //super.map(key, value, context);
            String info = value.toString() ;
            String[] words = info.split(" " ) ;
            // 2：第二步 将每个词做map操作并写入上下文中
            for (String word:words ) {
                // 2.1：map的输出等于Reduce的输入
                context.write(new Text(word), one );
            }

        }
    }

    /***
     * 归并操作计算
     */
    public static class MyReduce extends Reducer<Text,LongWritable,Text,LongWritable>{
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            //super.reduce(key, values, context);
            // 5：到了Reduce的时候，进来的残数据是  单个keys ，多个词
            Long sum = (long)0 ;
            for (LongWritable value : values ) {
                sum += value.get() ;
            }
            // 5.1：将Reduce计算出来的结果再写入到上下文，供输出时使用
            context.write(key, new LongWritable(sum) );
        }
    }

    //
    public static void main(String[] args ) throws Exception {
        Configuration conf = new Configuration() ;

        //升级处理，防止第二次运行，计算数据时报已存在异常，我们需要清除输出路径下面的文件的信息
        Path outputPath = new Path( args[1] ) ;
        FileSystem fileSystem = FileSystem.newInstance( conf ) ;
        //判断文件是否存在，存在的话，则递归删除
        if( fileSystem.exists( outputPath ) ) {
            fileSystem.delete( outputPath, true); //是否递归删除
            System.out.println( "递归删除已有输出目录.....----------" );
        }

        //创建job
        Job job = Job.getInstance( conf , "第一次运行MapReduce操作" ) ;

        //设置作业的处理类信息
        job.setJarByClass( WordCount2App.class );
        //设置作业的输入数据文件
        FileInputFormat.addInputPath( job , new Path( args[0] ) );

        //设置作业的mapper处理操作类 - 设置map处理后输出的数据类型
        job.setMapperClass( MyMapper.class );
        job.setMapOutputKeyClass( Text.class );
        job.setMapOutputValueClass( LongWritable.class );

        //设置作业的Reduce处理操作类 - 设置Reduce处理后输出的数据类型
        job.setReducerClass( MyReduce.class );
        job.setOutputKeyClass( Text.class );
        job.setOutputValueClass( LongWritable.class );

        //设置作业处理的输出文件路径
        FileOutputFormat.setOutputPath( job , new Path( args[1] ) );

        //提交MapReduce处理，并在处理成功后退出系统
        System.exit(  job.waitForCompletion( true ) ? 0 : 1 );
       ;
    }

}
