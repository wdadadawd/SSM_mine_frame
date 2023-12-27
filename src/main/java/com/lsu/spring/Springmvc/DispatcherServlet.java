package com.lsu.spring.Springmvc;

import com.lsu.spring.Factory.BeanFactory;
import com.lsu.spring.common.BeanMethod;
import com.lsu.spring.exceptions.DispatcherServletException;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * @author zt
 * @create 2022-07-19 19:03
 * 自定义中央处理器
 */
@WebServlet(urlPatterns = "/page/*")                              //处理所有的接口请求
public class DispatcherServlet extends HttpServlet {

    private BeanFactory beanFactory;

    private String staticPath;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = this.getServletContext();
        Object beanFactoryObj = servletContext.getAttribute("beanFactory");
        beanFactory = (BeanFactory) beanFactoryObj;
        staticPath = (String) servletContext.getAttribute("staticPath");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1.获取真正的请求地址和请求方法
        String requestUrl = req.getRequestURI().substring(5);              //获取真正的请求路径,截取掉前缀/page
        String requestMethod = req.getMethod();             //请求的方法GET、POST、DELETE、PUT等.......
//        Object objectBean = beanFactory.getBean(lastPath);                //通过Path获取对应的类
        //2.通过请求地址和请求方法匹配处理器和对应的方法
        BeanMethod beanMethod = beanFactory.getRequestBeanAndMethod(requestUrl, requestMethod);

        if (beanMethod == null) {       //没有匹配到接口
            //返回错误提示
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("404 - Not Found");
            return;
        }
        Object bean = beanMethod.getBean();
        String methodName = beanMethod.getMethodName();
//        System.out.println(bean.getClass().getName());
        CtClass ctClass = null;
        try {
            //获取字节码类
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();   // 获取线程上下文类加载器
            ClassPool classPool = new ClassPool(true);     // 使用线程上下文类加载器创建 ClassPool
            classPool.appendClassPath(new LoaderClassPath(classLoader)); // 添加线程上下文类加载器的类路径
            ctClass = classPool.get(bean.getClass().getName());// 使用 ClassPool 获取 CtClass 对象
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        //3.反射获取类的方法调用并处理返回值
        if (bean != null){
            try {
                // 获取第一个参数的名称
//                System.out.println(attr.variableName(1));
                Method[] methods = bean.getClass().getDeclaredMethods();
                for (Method m:methods){                       //遍历方法
//                    System.out.println(m.getName());
                    if (m.getName().equals(methodName)){                  //找到需要的方法
                        //
                        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
                        MethodInfo methodInfo = ctMethod.getMethodInfo();
                        CodeAttribute attribute =  methodInfo.getCodeAttribute();
                        LocalVariableAttribute attr = (LocalVariableAttribute) attribute.getAttribute(LocalVariableAttribute.tag);
                        //1.参数获取
                        Parameter[] parameters = m.getParameters();
                        Object[] paraValues = new Object[parameters.length];
                        for (int i = 0;i < parameters.length;i++){
                            String name = attr.variableName(Integer.parseInt(parameters[i].getName().substring(3))+1);
//                            System.out.println(name);
                            //如果是基础对象，直接赋值
                            if ("request".equals(name)){
                                paraValues[i] = req;
                            }else if("response".equals(name)){
                                paraValues[i] = resp;
                            }else if("session".equals(name)){
                                paraValues[i] = req.getSession();
                            }else{
                                String paraTypeName = parameters[i].getType().getName();
                                String value = req.getParameter(name);
//                                System.out.println(value);
                                if ("java.lang.Integer".equals(paraTypeName) && value!=null){
                                    paraValues[i] = Integer.parseInt(value);
                                }else if("java.lang.Double".equals(paraTypeName) && value!=null){
                                    paraValues[i] = Double.parseDouble(value);
                                }else{
                                    paraValues[i] = value;
                                }
                            }
                        }
                        //2.方法调用
                        m.setAccessible(true);
                        Object invoke = m.invoke(bean, paraValues);       //获取方法调用的放回值
                        //3.根据返回值进行视图处理,判断是否需要重定向或转发
                        if (invoke!=null){                                    //如果返回不等于null
                            String invokePath = (String) invoke;
                            if (invokePath.startsWith("redirect:")){              //如果是重定向
                                resp.sendRedirect(invokePath.substring("redirect:".length()));
                            }else if (invokePath.startsWith("json:")){             //如果是json格式数据
                                resp.setContentType("application/json;charset=utf-8");      //设置MIME类型
                                resp.getWriter().write(invokePath.substring("json:".length()));
                            }else {
                                //转发到视图
                                req.getRequestDispatcher(staticPath + "/" + invokePath + ".html").forward(req,resp);
                            }
                        }
                        return;       //结束遍历
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new DispatcherServletException("dispatcherServlet出错了");
            }
          }
    }
}
