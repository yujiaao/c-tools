package com.bixuebihui.spring;

import com.bixuebihui.util.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationTargetException;


/**
 * @author xwx
 */
public class BeanPostPrcessorImpl implements BeanPostProcessor {


    private String dbHelperBeanName = "dbHelper";


    // Bean 实例化之前进行的处理

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)

            throws BeansException {

        if (beanName.equals(getDbHelperBeanName())) {
            try {

                boolean res = Config.initDbConfig();
                if (res) {
                    System.out.println("Config.initDbConfig called by postProcessBeforeInitialization");
                } else {
                    System.err.println("Config.initDbConfig failed in postProcessBeforeInitialization");
                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return bean;

    }


    // Bean 实例化之后进行的处理

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        return bean;

    }


    public String getDbHelperBeanName() {
        return dbHelperBeanName;
    }


    public void setDbHelperBeanName(String dbHelperBeanName) {
        this.dbHelperBeanName = dbHelperBeanName;
    }

}
