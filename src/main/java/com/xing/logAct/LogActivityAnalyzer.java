package com.xing.logAct;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @Class LogActivityAnalyzer
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/18 11:12
 * @Copyright Copyright by 兴兴
 * @Direction 类说明
 *
 * 1: 先上传日志到linux，当然你要有一个早就能用的Hadoop运行环境
 *
 * 2: 上传日志文件：hadoop fs -put localhost_access_log.txt /hdfsapi/     要记得看是否上传成功了
 *
 * 3: 把本项目打包，上传至指定路径【我这里是上传至：/usr/hadoop/data/】
 *      我们需要使用的mvn打包方式，因为我们的外部框架 yauaa 没有包含在hadoop 中
 *      mvn assembly:assembly           到项目源码根目录下执行这个
 *
 *      copy: hadoopxing-1.0-SNAPSHOT-jar-with-dependencies.jar  to Linux
 *
 * 4: 命令 ： hadoop jar /usr/hadoop/data/hadoopxing-1.0-SNAPSHOT-jar-with-dependencies.jar com.xing.logAct.LogActivityAnalyzer hdfs://hadoop000:8020/hdfsapi/localhost_access_log.txt hdfs://hadoop000:8020/hdfsapi/output/log
 *
 * 5: 查看hadoop处理后的数据信息
 *          adoop fs -ls /hdfsapi/output/log
 *          hadoop fa -text /hdfsapi/output/log/part-r-00000
 *
 */
public class LogActivityAnalyzer {

    public static void main(String[] args) throws Exception{
        // 1：配置job
        Configuration conf = new Configuration() ;
        Job job = Job.getInstance( conf , "LogActivityAnalyzer" ) ;

        // 2：配置job启动jar的关联main
        job.setJarByClass( LogActivityAnalyzer.class );

        // 3：配置我们的 mapper 、reduce
        job.setMapperClass( LogMapper.class );
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setReducerClass( LogReduce.class );
        job.setOutputKeyClass( Text.class );
        job.setOutputValueClass( LongWritable.class );

        // 4：配置输入文件，输出文件
        FileInputFormat.addInputPath( job , new Path( args[0] ) );

        // 4.1：升级处理，防止第二次运行，计算数据时报已存在异常，我们需要清除输出路径下面的文件的信息
        Path outputPath = new Path( args[1] ) ;
        FileSystem fileSystem = FileSystem.newInstance( conf ) ;
        //判断文件是否存在，存在的话，则递归删除
        if( fileSystem.exists( outputPath ) ) {
            fileSystem.delete( outputPath, true); //是否递归删除
            System.out.println( "递归删除已有输出目录.....----------" );
        }
        FileOutputFormat.setOutputPath( job , outputPath );

        //提交MapReduce处理，并在处理成功后退出系统
        System.exit(  job.waitForCompletion( true ) ? 0 : 1 );
    }

}
