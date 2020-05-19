package com.example.scheduler.util;

import com.example.scheduler.config.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

public class BeanUtil {
    public static Object getBean(String beanName) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(beanName);
    }
}
