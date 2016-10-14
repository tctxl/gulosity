package com.opdar.gulosity.spring.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class GulosityNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("context", new ContextParser());
    }
}
