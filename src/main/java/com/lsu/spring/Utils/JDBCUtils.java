package com.lsu.spring.Utils;


import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;

/**
 * @author zt
 * @create 2022-07-14 22:43
 */
public class JDBCUtils {
    private static DataSource dataSource;

    /**
     * 创建连接池
     * @param dataSourceType 连接池类型
     * @param url 连接地址
     * @param username 用户名
     * @param password 密码
     * @param driverClassName 驱动类型
     */
    public static void createDataSource(String dataSourceType,String url, String username, String password,
                                              String driverClassName) {
        try {
            Class<?> dataSourceClass = Class.forName(dataSourceType);
            DataSource dataSource = (DataSource) dataSourceClass.getDeclaredConstructor().newInstance();
            // 设置连接池属性地址、用户名、密码、驱动
            setDataSourceProperty(dataSource, "url", url);
            setDataSourceProperty(dataSource, "username", username);
            setDataSourceProperty(dataSource, "password", password);
            setDataSourceProperty(dataSource, "driverClassName", driverClassName);
            JDBCUtils.dataSource = dataSource;
        } catch (Exception e) {
            System.err.println("创建连接池错误");
            e.printStackTrace();
            throw new RuntimeException("创建连接池错误", e);
        }
    }

    /**
     * 设置连接池属性
     * @param dataSource   数据池对象
     * @param propertyName 属性名
     * @param propertyValue 属性值
     */
    private static void setDataSourceProperty(DataSource dataSource, String propertyName, Object propertyValue) {
        try {
            // 获取属性的setter方法名
            String setterMethodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            // 获取属性的 setter 方法
            Method setterMethod = dataSource.getClass().getMethod(setterMethodName, propertyValue.getClass());
            // 调用 setter 方法设置属性值
            setterMethod.invoke(dataSource, propertyValue);
        } catch (Exception e) {
            System.err.println("设置属性" + propertyName + "错误");
            throw new RuntimeException("设置属性" + propertyName + "错误", e);
        }
    }

    public static Connection getConnection(){
        Connection c = null;
        try {
            c = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    //关闭资源
    public static void closeResource(PreparedStatement ps, Connection c, ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (c != null)
                c.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
