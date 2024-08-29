package com.dwk.enterprise.graphbuilder.util;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BeanService implements BeanFactoryAware {


    ListableBeanFactory beanFactory;


    public Object getBean(String name) {
        Object targetBean = null;
        try {
            targetBean = beanFactory.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {

        }

        if (targetBean == null) {
            throw new NoSuchBeanDefinitionException("No bean found called " + name);
        }
        return targetBean;
    }

    public Map getBeansByClass(Class type) {
        return beanFactory.getBeansOfType(type);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }


}
