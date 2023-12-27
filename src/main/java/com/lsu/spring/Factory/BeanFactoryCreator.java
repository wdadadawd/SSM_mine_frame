package com.lsu.spring.Factory;

import com.lsu.spring.Annotation.*;
import com.lsu.spring.Utils.StringUtil;
import com.lsu.spring.common.BeanMethod;
import com.lsu.spring.common.RequestMethodBean;
import com.lsu.spring.enums.RequestMethod;

import java.io.File;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zt
 * @create 2023-12-22 21:10
 * bean工厂实现类
 */
public class BeanFactoryCreator implements BeanFactory{

    private List<RequestMethodBean> requestList;    //存储请求的集合

    private Map<String,Object> beanMap;       //bean名和实例的映射(获取bean)

    private Map<String,Object> beanPathMap;   //类路径和实例的映射(方便后续的依赖注入)

    private Map<String,Class<?>> classMap;   //保存反射得到的Class(方便后续的依赖注入)

    public BeanFactoryCreator(String projectRoot,String path) throws Exception {
        beanMap = new HashMap<>();
        classMap = new HashMap<>();
        beanPathMap = new HashMap<>();
        requestList = new ArrayList<>();
        if (StringUtil.isEmpty(path)){
            path = "";
        }
        String sourceDirectory = projectRoot.replace("\\","/") + "/src/main/java"; // 拼接指定包目录
        // 1.遍历路径文件
        File sourceDir = new File(sourceDirectory,path.replace(".", "/"));
        if (sourceDir.exists() && sourceDir.isDirectory()) {      // 如果文件存在且为是目录，则使用队列扫描
            Queue<File> queue = new LinkedBlockingDeque();
            int sourceDirLength = sourceDirectory.length();
            queue.add(sourceDir);     //队列遍历文件夹
            while (!queue.isEmpty()){
                File nowFile = queue.poll();
                String fileName = nowFile.getName();
//                System.out.println(nowFile.getName());
                if (nowFile.isDirectory()){        //如果是文件夹将文件夹子文件加入队列中
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
                        if (clazz.isAnnotationPresent(Component.class)||clazz.isAnnotationPresent(Controller.class)||
                            clazz.isAnnotationPresent(Service.class)||clazz.isAnnotationPresent(Mapper.class)) {
                            createBean(clazz,classPathName,fileName);        //创建bean
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //4.反射设置类与类之间的依赖
            setClassDepend();
        }else{
            System.err.println("配置组件的包名有误");
            throw new RuntimeException("配置组件的包名有误");
        }
    }

    /**
     * 反射创建bean
     * @param clazz   反射class
     * @param classPathName 类路径
     * @param fileName 文件名
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void createBean(Class<?> clazz,String classPathName,String fileName) throws InstantiationException, IllegalAccessException {
        // 获取注解值(自定义bean名)
        String beanName = null;
        if (clazz.isAnnotationPresent(Component.class))
            beanName = clazz.getAnnotation(Component.class).beanName();
        else if (clazz.isAnnotationPresent(Controller.class)) {
            beanName = clazz.getAnnotation(Controller.class).beanName();
        }
        else if (clazz.isAnnotationPresent(Service.class))
            beanName = clazz.getAnnotation(Service.class).beanName();
        else if (clazz.isAnnotationPresent(Mapper.class))
            beanName = clazz.getAnnotation(Mapper.class).beanName();
        // 创建类的实例
        Object instance = clazz.newInstance();
        if (StringUtil.isEmpty(beanName))          //判断是否指定beanName,没指定则使用类名作为bean名
            beanName = fileName.substring(0, fileName.length()-5);   //得到类名
//          System.out.println(beanName);
        if (!beanMap.containsKey(beanName)&&!beanPathMap.containsKey(classPathName)){
            beanMap.put(beanName,instance);          //保存到beanMap中
            classMap.put(beanName,clazz);            //保存classMap
            beanPathMap.put(classPathName,instance); //保存到beanPathMap中
            for (Class<?> c : clazz.getInterfaces()) {  //将类实现的接口也映射到beanPath中
                if (!beanPathMap.containsKey(c.getName()))
                    beanPathMap.put(c.getName(), instance);
                else {
                    System.err.println("bean名重复");
                    throw new RuntimeException("bean名重复");
                }
            }
        }else{
            System.err.println("bean名重复");
            throw new RuntimeException("bean名重复");
        }
        if (clazz.isAnnotationPresent(Controller.class))
            setControllerRequests(clazz,beanName);
    }


    /**
     * 设置控制器的请求
     * @param clazz
     * @param beanName
     */
    public void setControllerRequests(Class<?> clazz,String beanName){
        Controller controllerAnno = clazz.getAnnotation(Controller.class);
        String rootUrl = controllerAnno.value();       //获取value路径
//        System.out.println("rootUrl:" + rootUrl);
        for (Method method : clazz.getDeclaredMethods()) {       //遍历方法,查找方法是否含有RequestMapping注解
            if (method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestAnno = method.getAnnotation(RequestMapping.class);
                RequestMethod[] requestMethods = requestAnno.method();
                String[] requestUrls = requestAnno.value();
                if (!StringUtil.isEmpty(rootUrl)){           //如果控制器前缀不为空,则给路径加上前缀
                    for (int i = 0;i < requestUrls.length;i++){
                        requestUrls[i] = rootUrl + requestUrls[i];
                    }
                }
                String methodName = method.getName();
                //记录请求信息(对应的控制器bean名、请求路径、请求方法、控制器方法名)
                requestList.add(new RequestMethodBean(beanName,requestUrls,requestMethods,methodName));
            }
        }
    }

    /**
     * 设置类依赖关系
     * @throws IllegalAccessException
     */
    public void setClassDepend() throws IllegalAccessException {
        for (String beanName : classMap.keySet()) {
            Class<?> clazz  = classMap.get(beanName);
            Object beanObj = beanMap.get(beanName);
            // 获取类的所有字段
            Field[] fields = clazz.getDeclaredFields();
            // 遍历字段，查找带有指定注解的属性
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)){
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    if (annotation.required()){
                        String value = annotation.value();
                        Object refObj;
                        if (StringUtil.isEmpty(value))     //如果未自定义bean的名称,则通过类路径匹配实例注入
                            refObj = beanPathMap.get(field.getType().getName());
                        else
                            refObj = beanMap.get(value);
//                            System.out.println(field.getType().getName());
                        //通过放射将refObj设置到beanObj的field属性上
                        field.setAccessible(true);
                        field.set(beanObj,refObj);
                    }
                }
            }
        }
    }

    @Override
    public Object getBean(String name) {
        return beanMap.get(name);
    }

    @Override
    public BeanMethod getRequestBeanAndMethod(String requestUrl, String requestMethod) {
        RequestMethodBean requestMethodBean = new RequestMethodBean(null, new String[]{requestUrl},
                new RequestMethod[]{RequestMethod.valueOf(requestMethod.toUpperCase())}, null);
        //遍历找到请求信息
        for (RequestMethodBean request :requestList){
            if (request.equals(requestMethodBean))
                return new BeanMethod(beanMap.get(request.getBeanName()),request.getMethodName());
        }
        return null;
    }
}
