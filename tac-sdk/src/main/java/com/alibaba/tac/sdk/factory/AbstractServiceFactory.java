package com.alibaba.tac.sdk.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * the factory class which provide tac engine data source
 */
public abstract class AbstractServiceFactory implements ApplicationContextAware {

    /**
     * the Spring contenxt
     */
    public static ApplicationContext applicationContext;



    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        if (applicationContext != null) {
            return;
        }
        // set the spring context
        applicationContext = ac;
    }

    /**
     * get spring bean with beanId;
     *
     * @param beanId spring bean id
     * @param <T>
     * @return
     */
    protected static <T> T getServiceBean(String beanId) {
        if (applicationContext.getBean(beanId) == null) {
            throw new IllegalStateException("bean[" + beanId + "] Incorrectly configured!");
        }
        return (T)applicationContext.getBean(beanId);
    }

    public static <T> T getServiceBean(Class<T> clazz) {

        T bean = applicationContext.getBean(clazz);

        checkNull(bean, clazz);

        return bean;
    }

    private static void checkNull(Object bean, Class<?> clazz) {

        if (bean == null) {
            String format = String.format("bean [ %s ] Incorrectly configured!", clazz.getName());
            throw new IllegalStateException(format);
        }
    }

}
