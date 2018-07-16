package com.xing.hadoop.filter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/****
 * 过滤 regex 格式的文件 ,如制定的后缀文件过滤
 */
public class RegexExcludePathFilter implements PathFilter {

    private final String regex;

    public RegexExcludePathFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean accept(Path path) {
        // TODO Auto-generated method stub
        boolean flag = path.toString().matches(regex);
        return !flag;
    }

}
