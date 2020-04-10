package com.juno.framework.netty.context;

import com.juno.framework.netty.annotation.NettyAutowired;
import com.juno.framework.netty.annotation.NettyController;
import com.juno.framework.netty.annotation.NettyService;
import com.juno.framework.netty.beans.NettyBeanWrapper;
import com.juno.framework.netty.beans.config.NettyBeanDefinition;
import com.juno.framework.netty.beans.support.NettyBeanDefinitionReader;
import com.juno.framework.netty.beans.support.NettyBeanFactory;
import com.juno.framework.netty.beans.support.NettyDefaultListableBeanFactory;
import com.juno.framework.netty.exception.NettyFwException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Juno
 * @Date: 2020/4/7 9:57
 */
public class NettyApplicationContext extends NettyDefaultListableBeanFactory implements NettyBeanFactory {

    private String scanPackage;
    private NettyBeanDefinitionReader reader;

    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, NettyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    public NettyApplicationContext(ApplicationContext context, String scanPackage) {
        setApplicationContext(context);
        this.scanPackage = scanPackage;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 定位，扫描配置文件
        reader = new NettyBeanDefinitionReader(this.scanPackage);
        // 加载配置文件
        List<NettyBeanDefinition> nettyBeanDefinitions = reader.loadBeanDefinitions();
        // 注册，把配置信息存到 IOC 容器中
        doRegisterBeanDefinition(nettyBeanDefinitions);
        // 把不是延时加载的类，提前初始化
        doAutowired();
    }

    private void doRegisterBeanDefinition(List<NettyBeanDefinition> nettyBeanDefinitions) {
        for (NettyBeanDefinition nettyBeanDefinition : nettyBeanDefinitions) {
            if (super.beanDefinitionMap.containsKey(nettyBeanDefinition.getFactoryBeanName())) {
                throw new NettyFwException("The “" + nettyBeanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(nettyBeanDefinition.getFactoryBeanName(),nettyBeanDefinition);
        }
    }

    private void doAutowired() {
        for (Map.Entry<String, NettyBeanDefinition> entry : super.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if (!entry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        if (NettyBeanDefinitionReader.MyNettySelfControllerName.equals(beanName)) {
            return null;
        }
        NettyBeanDefinition nettyBeanDefinition = this.beanDefinitionMap.get(beanName);
        // 初始化
        Object instance = instantiateBean(nettyBeanDefinition);
        if (null == instance) {
            return instance;
        }
        NettyBeanWrapper nettyBeanWrapper = new NettyBeanWrapper(instance);
        // 将 nettyBeanWrapper 存到 IOC 容器中
        this.factoryBeanInstanceCache.put(beanName,nettyBeanWrapper);
        // 注入
        populateBean(nettyBeanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(NettyBeanWrapper nettyBeanWrapper) {
        Object instance = nettyBeanWrapper.getWrappedInstance();
        Class<?> clazz = nettyBeanWrapper.getWrappedClass();
        if (!(clazz.isAnnotationPresent(NettyController.class) ||
                clazz.isAnnotationPresent(NettyService.class))) {
            return;
        }
        // 遍历所有的 field
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (!(field.isAnnotationPresent(NettyAutowired.class) || field.isAnnotationPresent(Autowired.class) || field.isAnnotationPresent(Resource.class))) {
                    continue;
                }
                field.setAccessible(true);
//                NettyAutowired autowired = field.getAnnotation(NettyAutowired.class);
//                if (!autowired.required()) {
//                    continue;
//                }
                String fieldName = field.getType().getName();
                if (null == this.factoryBeanInstanceCache.get(fieldName)) {
                    // 从 spring IOC 容器中查找
                    Object bean = this.applicationContext.getBean(field.getType());
                    this.factoryBeanObjectCache.put(fieldName,bean);
                    NettyBeanWrapper beanWrapper = new NettyBeanWrapper(bean);
                    this.factoryBeanInstanceCache.put(fieldName,beanWrapper);
                }

                if (null == this.factoryBeanInstanceCache.get(fieldName)) {
                    continue;
                }

                field.set(instance,this.factoryBeanInstanceCache.get(fieldName).getWrappedInstance());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Object instantiateBean(NettyBeanDefinition nettyBeanDefinition) {
        String beanName = nettyBeanDefinition.getBeanClassName();
        Object instance = null;
        try {
            // 默认单例
            if (factoryBeanObjectCache.containsKey(beanName)) {
                instance = factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> clazz = Class.forName(beanName);
                if (clazz.isAnnotationPresent(NettyController.class) || clazz.isAnnotationPresent(NettyService.class)) {
                    instance = clazz.newInstance();
                    factoryBeanObjectCache.put(beanName,instance);
                    factoryBeanObjectCache.put(nettyBeanDefinition.getFactoryBeanName(),instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


}
