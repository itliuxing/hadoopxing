package com.xing.hadoop;

import com.xing.hadoop.filter.RegexAcceptPathFilter;
import com.xing.hadoop.filter.RegexExcludePathFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

/***
 * 小文件 合并 成大文件，然后打包上传
 *
 * 参考文档：https://www.cnblogs.com/braveym/p/7583218.html
 */
public class MergeSmallFilesToHDFS {

    public static String  hdfsUrl = "hdfs://192.168.248.131:8020" ;
    FileSystem fileSystem = null ;
    Configuration config = null ;
    FileSystem local = null ;

    @Test
    public void smallFilesMerge()throws Exception{
        // 获得本地文件系统
        local = FileSystem.getLocal( config );
        //过滤目录下的 svn 文件，globStatus从第一个参数通配符合到文件，剔除满足第二个参数到结果，因为PathFilter中accept是return!
        FileStatus[] dirstatus = local.globStatus(new Path("E://codeGenra/logs/*"),new RegexExcludePathFilter("^.*txt$") );
        //获取73目录下的所有文件路径，注意FIleUtil中stat2Paths()的使用，它将一个FileStatus对象数组转换为Path对象数组。
        Path[] dirs = FileUtil.stat2Paths(dirstatus);
        FSDataOutputStream out = null;
        FSDataInputStream in = null;

        for (Path dir : dirs) {
            pathHandler( out, in ,dir );
        }

    }

    public void pathHandler(  FSDataOutputStream out, FSDataInputStream in,Path  dir)throws  Exception{
        String fileName = dir.getName().replace("-", "");//文件名称
        //只接受日期目录下的.log文件，^匹配输入字符串的开始位置,$匹配输入字符串的结束位置,*匹配0个或多个字符。
        FileStatus[] localStatus = local.globStatus(new Path(dir+"/*"),new RegexAcceptPathFilter("^.*txt$"));
        // 获得日期目录下的所有文件
        Path[] listedPaths = FileUtil.stat2Paths(localStatus);
        //输出路径
        Path block = new Path( hdfsUrl + "/hdfsapi/mergesmall/"+ fileName + ".txt");
        // 打开输出流
        out = fileSystem.create(block);
        for (Path p : listedPaths) {
            in = local.open(p);// 打开输入流
            IOUtils.copyBytes(in, out, 4096, false); // 复制数据，IOUtils.copyBytes可以方便地将数据写入到文件，不需要自己去控制缓冲区，也不用自己去循环读取输入源。false表示不自动关闭数据流，那么就手动关闭。
            // 关闭输入流
            in.close();
        }
        if (out != null) {
            // 关闭输出流
            out.close();
        }
    }


    @After
    public void tear()throws Exception{
        config = null ;
        fileSystem = null ;

        System.out.println( hdfsUrl + " is destory" );
    }

    @Before
    public void setup()throws Exception{
        config = new Configuration() ;
        fileSystem = FileSystem.get( new URI( hdfsUrl ) , config ,"hadoop") ;

        System.out.println( hdfsUrl + " is install" );
    }
}
