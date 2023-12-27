package com.lsu.spring.Factory;

import com.lsu.spring.Annotation.MyWebFilter;
import com.lsu.spring.Utils.StringUtil;

import javax.servlet.Filter;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zt
 * @create 2023-12-22 20:50
 * 过滤器工厂实现类
 */
public class FilterFactoryCreator implements FilterFactory{

    private Map<String,ClassInfo> filterMap;

    private List<Filter> filterList;

    public FilterFactoryCreator(String projectRoot,String path) throws Exception {
        filterMap = new HashMap<>();
        filterList = new ArrayList<>();
        if (StringUtil.isEmpty(path)){
            path = "";
        }
        String sourceDirectory = projectRoot.replace("\\","/") + "/src/main/java"; // 拼接指定包目录
        // 1.遍历路径文件
        File sourceDir = new File(sourceDirectory,path.replace(".", "/"));
        if (sourceDir.exists() && sourceDir.isDirectory()) {   // 如果文件存在且为是目录，则使用队列扫描
            Queue<File> queue = new LinkedBlockingDeque();
            int sourceDirLength = sourceDirectory.length();
            queue.add(sourceDir);     //队列遍历文件夹
            while (!queue.isEmpty()){
                File nowFile = queue.poll();
                String fileName = nowFile.getName();
//                System.out.println(nowFile.getName());
                if (nowFile.isDirectory()){        //如果是文件夹
                    File[] files = nowFile.listFiles();
                    queue.addAll(Arrays.asList(files));
                }else if (fileName.endsWith(".java")){
                    try {
                        //2.获取类路径，使用反射创建Class对象
                        String absolutePath = nowFile.getAbsolutePath();     //获取文件绝对路径
                        String classPathName = absolutePath.substring(sourceDirLength + 1,absolutePath.length()-5)
                                .replace("\\",".");     //截取、替换得到类路径
//                        System.out.println(classPathName);
                        Class<?> clazz = Class.forName(classPathName,true, Thread.currentThread().getContextClassLoader());
                        // 3.判断类是否有指定的注解,如果有创建对象
                        if (clazz.isAnnotationPresent(MyWebFilter.class)) {
                            // 获取注解
                            MyWebFilter annotation = clazz.getAnnotation(MyWebFilter.class);
                            String beanName = annotation.beanName();
                            int priority = annotation.priority();
                            // 创建类的实例
                            Filter instance = (Filter) clazz.newInstance();
                            if (StringUtil.isEmpty(beanName))          //如果未自定义bean的名称,则通过类路径匹配实例注入
                                beanName = fileName.substring(0, fileName.length()-4);   //得到类名
                            filterMap.put(beanName,new ClassInfo(instance,priority));//保存到map中
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 4.遍历完成,按照优先级排序转换为数组
            List<ClassInfo> classInfoList = new ArrayList(filterMap.values());
            // 按照优先级排序
            classInfoList.sort(Comparator.comparingInt(ClassInfo::getPriority));
            for (ClassInfo classInfo : classInfoList) {
                filterList.add(classInfo.getInstance());
            }
//            System.out.println(filterList.size());
        }else{
            System.err.println("配置过滤器的包名有误");
            throw new RuntimeException("配置过滤器的包名有误");
        }
    }

    @Override
    public List<Filter> getFilters() {
        return filterList;
    }

    @Override
    public Filter getFilters(String filterName) {
        return filterMap.get(filterName).getInstance();
    }
}
class ClassInfo {
    private Filter instance;
    private int priority;
    public ClassInfo(Filter instance, int priority) {
           this.instance = instance;
           this.priority = priority;
    }
    public int getPriority() {
        return this.priority;
    }
    public Filter getInstance() {
        return this.instance;
    }
}
