package com.xing.hadoop;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.util.ConfigUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;


/***
 * HDFSAPI 操作防火墙开出来
 *
 * 端口一定要开启：50010  50020  50070  8020
 */
public class HDFSAPI {

    public static String  hdfsUrl = "hdfs://192.168.248.131:8020" ;
    FileSystem fileSystem = null ;
    Configuration config = null ;

    //ConfigUtil

    /***
     * 创建文件夹
     * @throws Exception
     */
    @Test
    public void mkdir()throws Exception{
        fileSystem.mkdirs( new Path("/hdfsapi/test") ) ;
    }

    /***
     * 创建txt文件并写入内容
     * @throws Exception
     */
    @Test
    public void createFile() throws Exception{
        //public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize)
        Path path = new Path("/hdfsapi/test/liuxing1.txt") ;
        //FSDataOutputStream fsDataOutputStream = fileSystem.create( path , true ,1024,(short)1,10485760  ) ;
        FSDataOutputStream fsDataOutputStream = fileSystem.create( path ) ;
        //创建成功了，然后写入内容的时候出错了----start
        byte[] readBuf = " hadoop ,Hello World".getBytes("UTF-8");
        fsDataOutputStream.write(readBuf, 0, readBuf.length);
        fsDataOutputStream.close();
        /*fsDataOutputStream.write( " hhelloword".getBytes() );
        fsDataOutputStream.write("how are you".getBytes());*/
        //fsDataOutputStream.flush();
        //创建成功了，然后写入内容的时候出错了----stop
        //fsDataOutputStream.close();
    }

    /***
     * 查看txt文件
     * @throws Exception
     */
    @Test
    public void catFile() throws Exception{
        FSDataInputStream inputStream = fileSystem.open(  new Path("/hdfsapi/test/liuxing1.txt") ) ;
        IOUtils.copyBytes( inputStream , System.out ,2014 );
        inputStream.close();
        System.out.println("");
    }

    /***
     * 重命名txt文件
     * @throws Exception
     */
    @Test
    public void rename() throws Exception{
        Path oldPath = new Path("/hdfsapi/test/liuxing1.txt") ;
        Path newPath = new Path("/hdfsapi/test/liuxing2.txt") ;
        fileSystem.rename(  oldPath ,newPath ) ;
    }

    /***
     * 下载txt文件
     * @throws Exception
     */
    @Test
    public void download() throws Exception{
        Path srcPath = new Path("/hdfsapi/test/liuxing1.txt") ;
        Path localPath = new Path("D://liuxing.txt") ;
        fileSystem.copyFromLocalFile(  srcPath ,localPath ); ;
    }

    /***
     * 删除txt文件
     * @throws Exception
     */
    @Test
    public void delete() throws Exception{
        Path srcPath = new Path("/hdfsapi/test/liuxing1.txt") ;
        fileSystem.delete(  srcPath ,false ); //是否递归删除
        srcPath = new Path("/hdfsapi/mergesmall") ;
        fileSystem.delete(  srcPath ,true ); //是否递归删除
    }

    /***
     * 查看文件夹并打印遍历里面的文件,就像 Linux 的ll 命令一样
     * @throws Exception
     */
    @Test
    public void listStatus() throws Exception{
        FileStatus[] array = fileSystem.listStatus(  new Path("/hdfsapi/test") ) ;
        for (int i = 0; i < array.length; i++) {
            Boolean isDir = array[i].isDirectory() ;
            String dirs = isDir ? "文件夹" : "文件" ;
            String path = array[i].getPath().toString() ;
            short replication = array[i].getReplication() ;
            long length = array[i].getLen();
            System.out.println( dirs + "\t" +replication+ "\t" +length+ "\t" + path );

        }

//打印结果：3 是要有3个副本，JAVA_API 上传文件是3个副本，而Hadoop 自己创建或者上传则是一个副本。因此上面的问题我们就知道是怎么回事了，等下写个文件说明放到CSDN
//        文件	3	0	hdfs://192.168.248.131:8020/hdfsapi/test/liuxing1.txt
//        文件	3	0	hdfs://192.168.248.131:8020/hdfsapi/test/liuxing5.txt
    }

    /***
     * 重命名txt文件
     * @throws Exception
     */
    @Test
    public void copyFileTo() throws Exception{
        Path localPath = new Path("F:\\msdia80.dll") ;
        Path hdfsPath = new Path("/hdfsapi/test") ;
        fileSystem.copyFromLocalFile(  localPath , hdfsPath );
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

    public static void main(String[] args) {
        System.out.println( hdfsUrl );

        App app = new App();
        StringUtils.isNotEmpty( hdfsUrl ) ;

        Integer ints = new Integer(1) ;

    }

}
