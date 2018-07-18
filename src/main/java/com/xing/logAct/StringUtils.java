package com.xing.logAct;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Class StringUtils
 * @Author 作者姓名:刘兴
 * @Version 1.0
 * @Date 创建时间：2018/7/18 10:18
 * @Copyright Copyright by 兴兴
 * @Direction 类说明   字符串操作工具类
 */
public class StringUtils {

    /***
     * 获取字符串中，指定字符，出现第指定次数的坐标
     * @param string
     * @return
     */
    public static int getCharacterPosition( String string , String chars , Integer index ){
        //这里是获取"/"符号的位置
        Matcher slashMatcher = Pattern.compile( chars ).matcher(string);
        int mIdx = 0;
        while(slashMatcher.find()) {
            mIdx++;
            //当"/"符号第三次出现的位置
            if( mIdx == index ){
                break;
            }
        }
        return slashMatcher.start();
    }

}
