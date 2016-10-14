package com.opdar.gulosity.spring.namespace;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.spring.configs.Configuration;
import com.opdar.gulosity.spring.configs.JdbcConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class ListenerParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        NodeList nodes = element.getChildNodes();
        for(int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);
            if(node instanceof Element){
                if(node.getNodeName().equals("gulosity:list")){
                    NodeList listenerNodes = node.getChildNodes();
                    for(int j=0;j<listenerNodes.getLength();j++){
                        Node listenerNode = listenerNodes.item(j);
                        if(listenerNode instanceof Element){
                            String className = ((Element) listenerNode).getAttribute("class");
                            try {
                                Class clz = Class.forName(className);
                                MysqlContext.addRowCallback((RowCallback) clz.newInstance());
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
