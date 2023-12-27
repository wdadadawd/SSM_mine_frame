package com.lsu.spring.base;

import com.lsu.spring.Utils.JDBCUtils;
import com.lsu.spring.Utils.StringUtil;
import com.lsu.spring.exceptions.DAOException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author zt
 * @create 2022-07-14 22:56
 */
public abstract class BaseDAO<T> {
    private Connection c = null;


    private Class<T> clazz = null;
    {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType genericSuperclass1 = (ParameterizedType) genericSuperclass;

        Type[] t = genericSuperclass1.getActualTypeArguments();   //获取父类的泛型参数
        clazz = (Class<T>)t[0];
    }

    //通用增删改
    public int update( String sql, Object ...args){
        c = JDBCUtils.getConnection();
        PreparedStatement ps = null;  //预编译sql语句
        try {

            ps = c.prepareStatement(sql);

            for (int i = 1;i <= args.length;i++){            //设置参数
//                ps.setString(i,String.valueOf(args[i-1]));
                ps.setObject(i,args[i-1]);
            }

            // ps.execute();                                //执行
            return ps.executeUpdate();                     //返回本次操作影响的行数
        } catch (SQLException e) {
            throw new DAOException("DAO update出问题了");
        } finally {
            JDBCUtils.closeResource(ps,null,null);                        //关闭资源
        }
    }
    //通用查询
    public ArrayList<T> query(String sql, Object ...args) {
        c = JDBCUtils.getConnection();
        ArrayList<T> al = new ArrayList<>();
        PreparedStatement ps = null;           //预编译sql语句
        ResultSet resultSet = null;                 //获取结果集
        try {
            ps = c.prepareStatement(sql);

            for (int i = 1;i <= args.length;i++){
            //    ps.setString(i,String.valueOf(args[i-1]));                //填充占位符
                ps.setObject(i,args[i-1]);
            }

            resultSet = ps.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();    //获取结果集的元数据
            int columnCount = metaData.getColumnCount();             //获取列数

            while(resultSet.next()){                                //next判断结果集是否有下条数据
                T t = clazz.newInstance();

                for (int i = 1;i <= columnCount;i++){
                    Object o = resultSet.getObject(i);                     //获取字段值
                    String columnName = metaData.getColumnName(i);         //获取字段名
                    //getColumnLabel()获取列的当前名(包括别名)

                    //当字段名和属性名不同时，可以给表设置别名，通过别名来获取对应的属性
                    Field field = clazz.getDeclaredField(StringUtil.columnNameFormat(columnName));    //根据字段名获取对应的属性
                    field.setAccessible(true);           //设置private也可以改变值
                    field.set(t,o);                    //设置属性值
                }
                al.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("DAO update出问题了");
        } finally {
            JDBCUtils.closeResource(ps,null,resultSet);             //关闭资源
        }
        if (al.size() > 0)
          return al;
        else
            return null;
    }

    public  <E> E getValue(String sql,Object ...args){
        c = JDBCUtils.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = c.prepareStatement(sql);
            for (int i = 1;i <= args.length;i++){
//                ps.setString(i,String.valueOf(args[i-1]));
                ps.setObject(i,args[i-1]);
            }
            rs = ps.executeQuery();
            if(rs.next()){
                return (E)rs.getObject(1);
            }

        } catch (SQLException throwables) {
            throw new DAOException("DAO update出问题了");
        } finally {
            JDBCUtils.closeResource(ps,null,rs);
        }
        return null;
    }
}
