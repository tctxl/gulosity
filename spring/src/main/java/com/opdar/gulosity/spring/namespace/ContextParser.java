package com.opdar.gulosity.spring.namespace;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.base.TableCache;
import com.opdar.gulosity.spring.annotations.Table;
import com.opdar.gulosity.spring.configs.Configuration;
import com.opdar.gulosity.spring.configs.JdbcConfiguration;
import com.opdar.gulosity.spring.listeners.DefaultAutoMappingListener;
import com.opdar.gulosity.utils.ResourceUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        String serverId = element.getAttribute("serverId");
        if(serverId == null)serverId = "1000";
        NodeList nodes = element.getChildNodes();
        String packageName = null;
        for(int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);
            if(node instanceof Element){
                if(node.getNodeName().equals("gulosity:mapping")){
                    packageName = ((Element) node).getAttribute("package");
                }else
                if(node.getNodeName().equals("gulosity:listeners")){
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

        JdbcConfiguration jdbcConfiguration = new JdbcConfiguration(host, Integer.valueOf(port), userName, passWord, defaultDatabaseName,Long.valueOf(serverId));

        final String finalPackageName = packageName;
        ResourceUtils.find(new ResourceUtils.FileFinder() {
            @Override
            public String suffix() {
                return ".class";
            }

            @Override
            public String getPackageName() {
                return finalPackageName == null?"": finalPackageName;
            }

            @Override
            public void call(String packageName, String file, String fullName) {
                try {
                    Class<?> clz = Class.forName(packageName + file);
                    Table table = clz.getAnnotation(Table.class);
                    if(table!= null){
                        DefaultAutoMappingListener.putClass(table.value(),clz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        jdbcConfiguration.init();
        builder.addPropertyValue("jdbcConfiguration", jdbcConfiguration);

    }
}
