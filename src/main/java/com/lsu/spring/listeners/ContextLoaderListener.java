package com.lsu.spring.listeners;

import com.lsu.spring.Factory.BeanFactory;
import com.lsu.spring.Factory.BeanFactoryCreator;
import com.lsu.spring.Factory.FilterFactory;
import com.lsu.spring.Factory.FilterFactoryCreator;
import com.lsu.spring.Utils.JDBCUtils;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.util.Map;

/**
 * @author zt
 * @create 2023-12-24 19:28
 */
@WebListener
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        try {
            parsingYaml(servletContext);
        } catch (Exception e) {
            System.out.println("配置解析文件错误");
        }
    }

    /**
     * 解析yaml文件
     * @param servletContext servlet上下文
     * @throws Exception
     */
    public void parsingYaml(ServletContext servletContext) throws Exception {
        //类根路径
        String appPath = servletContext.getRealPath("/").substring(0,servletContext.getRealPath("/").indexOf("\\target"));
        // 指定 YAML 文件的路径（相对于类路径）
        String yamlFilePath = "application.yaml";
        // yaml配置文件的输入流
        InputStream inputStream = FilterFactoryCreator.class.getClassLoader().getResourceAsStream(yamlFilePath);
        if (inputStream != null) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);       //加载yaml数据
            if (yamlData != null){
                if (yamlData.containsKey("spring")){              //判断是否包含spring配置
                    Map<String, Object> springDate = (Map<String, Object>)yamlData.get("spring");
                    if (springDate.containsKey("scan-location")){ //判断是否包含扫描地址的配置
                        Map<String, Object> scanDate = (Map<String, Object>) springDate.get("scan-location");
                        String filterPath = (String) scanDate.get("filter-location");
                        String beanPath = (String) scanDate.get("filter-location");
                        //创建Bean、Filter工厂并保存在servlet上下文中
                        BeanFactory b = new BeanFactoryCreator(appPath,beanPath);
                        FilterFactory f = new FilterFactoryCreator(appPath,filterPath);
                        servletContext.setAttribute("beanFactory",b);
                        servletContext.setAttribute("filterFactory",f);
                    }
                    if (springDate.containsKey("datasource")){          //判断是否包含数据库连接信息
                        //获取连接信息
                        Map<String, Object> dataSource = (Map<String, Object>) springDate.get("datasource");
                        String url = (String) dataSource.get("url");
                        String userName = (String) dataSource.get("username");
                        String password = (String) dataSource.get("password");
                        String type = (String) dataSource.get("type");
                        String driverClassName = (String) dataSource.get("driver-class-name");
                        //创建连接池
                        JDBCUtils.createDataSource(type,url,userName,password,driverClassName);
//                        System.out.println("数据库连接信息:" +
//                                url + "," + userName + "," + password + "," + type + "," + driverClassName);
                    }else{
                        System.err.println("未配置数据库连接信息");
                        throw new RuntimeException("未配置数据库连接信息");
                    }
                    if (springDate.containsKey("static")){
                        Map<String, Object> staticDate = (Map<String, Object>) springDate.get("static");
                        String staticPath = (String) staticDate.get("url");
                        servletContext.setAttribute("staticPath",staticPath);
                    }
                }
            }
        } else {
            System.err.println("application.yaml文件不存在");
            throw new RuntimeException("application.yaml文件不存在");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
