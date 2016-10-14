package com.opdar.gulosity.spring.namespace;

import com.opdar.gulosity.spring.configs.Configuration;
import com.opdar.gulosity.spring.configs.JdbcConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class ContextParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String userName = element.getAttribute("userName");
        String passWord = element.getAttribute("passWord");
        String defaultDatabaseName = element.getAttribute("defaultDatabaseName");
        JdbcConfiguration jdbcConfiguration = new JdbcConfiguration(host, Integer.valueOf(port), userName, passWord, defaultDatabaseName);
        jdbcConfiguration.init();
        builder.addPropertyValue("jdbcConfiguration", jdbcConfiguration);

    }
}
