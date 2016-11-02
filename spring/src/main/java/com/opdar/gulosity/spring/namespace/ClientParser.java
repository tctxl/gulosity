package com.opdar.gulosity.spring.namespace;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.spring.configs.Configuration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class ClientParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String uid = element.getAttribute("uid");
        try {
            Class<?> clientClz = Class.forName("com.opdar.gulosity.replication.client.Client");
            Object client = clientClz.newInstance();
            initListeners(element, clientClz, client);
            Method openMethod = ReflectionUtils.findMethod(clientClz, "open", InetSocketAddress.class);
            openMethod.invoke(client,new InetSocketAddress(host, Integer.valueOf(port)));

            Method requestPosMethod = null;
            if(uid != null){
                requestPosMethod = ReflectionUtils.findMethod(clientClz, "requestPos",String.class);
                requestPosMethod.invoke(client,uid);
            }else{
                requestPosMethod = ReflectionUtils.findMethod(clientClz, "requestPos");
                requestPosMethod.invoke(client);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners(Element element, Class<?> clientClz, Object client) {
        Method addRowCallbackMethod = ReflectionUtils.findMethod(clientClz, "addRowCallback",RowCallback.class);
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals("gulosity:listeners")) {
                    NodeList listenerNodes = node.getChildNodes();
                    for (int j = 0; j < listenerNodes.getLength(); j++) {
                        Node listenerNode = listenerNodes.item(j);
                        if (listenerNode instanceof Element) {
                            String className = ((Element) listenerNode).getAttribute("class");
                            try {
                                Class clz = Class.forName(className);
                                RowCallback rowCallback = (RowCallback) clz.newInstance();
                                addRowCallbackMethod.invoke(client,rowCallback);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
