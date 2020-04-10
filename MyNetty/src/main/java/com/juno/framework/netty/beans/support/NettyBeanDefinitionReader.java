package com.juno.framework.netty.beans.support;


import com.juno.framework.netty.beans.config.NettyBeanDefinition;
import com.juno.framework.netty.utils.TransferUtils;
import com.juno.framework.netty.web.MyNettySelfController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: Juno
 * @Date: 2020/4/7 10:02
 */
public class NettyBeanDefinitionReader {

    //固定配置文件中的key，相对于xml的规范
//    private final String SCAN_PACKAGE = "scan-package";
    public static final String MyNettySelfControllerName = "myNettySelfController";
    private List<String> registryBeanClasses = new ArrayList<String>();

//    private Properties config = new Properties();

    public NettyBeanDefinitionReader(String scanPackage) {
//        String path = locations[0].replace("classpath:", "");
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
//        try {
//            config.load(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != is) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        registryBeanClasses.add(MyNettySelfController.class.getName());
        doScanner(scanPackage);
    }

    private void doScanner(String scanPackage) {
        String name = "/" + scanPackage.replaceAll("\\.","/");
        URL url = this.getClass().getResource(name);
        File classPath = new File(url.getFile());
        File[] files = classPath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class","");
                registryBeanClasses.add(className);
            }
        }
    }

    public List<NettyBeanDefinition> loadBeanDefinitions() {
        List<NettyBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isInterface()) {
                    continue;
                }
                //beanName有三种情况:
                //1、默认是类名首字母小写
                //2、自定义名字
                //3、接口注入
                result.add(doCreateBeanDefinition(TransferUtils.toLowerFirstCase(clazz.getSimpleName()),clazz.getName()));

                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> i : interfaces) {
                    // 有多个实现类，会覆盖
                    // 可以用自定义类名
                    result.add(doCreateBeanDefinition(i.getName(),clazz.getName()));
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    //把每一个配信息解析成一个BeanDefinition
    private NettyBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        NettyBeanDefinition beanDefinition = new NettyBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }


}
