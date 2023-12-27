package com.lsu.spring.Utils;

/**
 * @author zt
 * @create 2022-07-16 21:13
 */
public class StringUtil {
    public static boolean isEmpty(String s){
        return s == null || "".equals(s);
    }


    /**
     * 字段名和属性名的转换      (_转为驼峰)
     * @param columnName
     * @return
     */
    public static String columnNameFormat(String columnName){
        String result = "";
        int index = 0;
        //循环遍历,直到遍历所有的_
        while (index < columnName.length() && columnName.indexOf("_",index)!=-1){
            index = columnName.indexOf("_");
            result += columnName.substring(0, index);
            if (index + 1 < columnName.length())
                result+=Character.toUpperCase(columnName.charAt(index+1));
            index+=2;
        }
        //加上剩余的字符串
        if (index < columnName.length()){
            result+=columnName.substring(index);
        }
        return result;
    }
}
