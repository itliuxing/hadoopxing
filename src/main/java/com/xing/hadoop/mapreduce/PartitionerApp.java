package com.xing.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @Class 	PartitionerApp.java
 * @Author 	作者姓名:刘兴
 * @Version	1.0
 * @Date	创建时间：2018-7-15 下午3:12:11
 * @Copyright Copyright by 兴兴
 * @Direction 类说明       使用MapReduce开发wordcount应用程序，本地做一次Reduce
 *
 * Hadoop处理命令：
 *      hadoop jar /usr/hadoop/data/hadoopxing-1.0-SNAPSHOT.jar com.xing.hadoop.mapreduce.CombinerApp hdfs://hadoop000:8020/hdfsapi/hello.txt hdfs://hadoop000:8020/hdfsapi/output/
 */
/***
 * 文本数据-销售数据，我们的目的是需要对每个品牌的数据做大度处理，这个做数据放到Hadoop hdfs上面做数据分析
 *
 * xiaomi  200  2018-07-11
 * huawei 2300  2018-07-11
 * meizu 1001  2018-07-11
 * nokia 10  2018-07-11
 * xiaomi 112  2018-07-12
 * huawei 113  2018-07-12
 * meizu 11  2018-07-12
 * nokia 9  2018-07-12
 *
 */
public class PartitionerApp {

    /****
     * 每行字符的分割点几点计算
     */
    public static class MyMapper extends Mapper<LongWritable,Text,Text ,LongWritable>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 1：第一步分割传入的行数据
            //super.map(key, value, context);
            String info = value.toString() ;
            String[] words = info.split(" " ) ;
            // 2：第二步 将每个词做map操作并写入上下文中
            // 2.1：map的输出等于Reduce的输入
            context.write( new Text( words[0] ), new LongWritable(Long.parseLong( words[1] ))  );

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

    /***
     * partitioner处理
     */
    public static class MyPartitioner extends Partitioner<Text,LongWritable>{
        @Override
        public int getPartition(Text text, LongWritable longWritable, int numPartitions) {
            if( text.equals( "xiaomi" ) ){
                return 0 ;
            }else if( text.equals( "huawei" ) ){
                return 1 ;
            }else if( text.equals( "meizu" ) ){
                return 2 ;
            }
            return 3 ;
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
        job.setJarByClass( PartitionerApp.class );
        //设置作业的输入数据文件
        FileInputFormat.addInputPath( job , new Path( args[0] ) );

        //设置作业的mapper处理操作类 - 设置map处理后输出的数据类型
        job.setMapperClass( MyMapper.class );
        job.setMapOutputKeyClass( Text.class );
        job.setMapOutputValueClass( LongWritable.class );

        //本地Reduce计算处理-跟我们上面的数据的partitioner 数量一致就可以了
        job.setPartitionerClass( MyPartitioner.class );
        job.setNumReduceTasks( 4 );

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
