package com.bixuebihui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.URL;

/**
 * @author xwx
 */
public class SpringBeanZK {
    private GenericApplicationContext beanFactory;


    private String beanConfigFile = "beans.xml";
    private static final String CONFIG_PROPERTIES = "beans.properties";
    private static final Log LOG = LogFactory.getLog(SpringBeanZK.class);


    public Object getBean(String resourceId) {
        return getXMLlBeanFactory().getBean(resourceId);
    }


    public boolean isSinglton(String resourceId) {
        return getXMLlBeanFactory().isSingleton(resourceId);
    }

    public String[] getBeanDefinitionNames() {
        return getXMLlBeanFactory().getBeanDefinitionNames();
    }

    public void destroy() {
        if (beanFactory != null) {
            beanFactory.destroy();
            beanFactory = null;
        }
    }

    private ApplicationContext getXMLlBeanFactory() {
        if (beanFactory == null) {
            initFactory();
        }
        return beanFactory;
    }

    /**
     * use log4j Loader
     */
    private synchronized void initFactory() {
        if (beanFactory == null) {

            Resource resource = null;

            URL url = Loader.getResource(beanConfigFile);
            if (url == null) {
                url = this.getClass().getResource("/" + beanConfigFile);
                LOG.info(this.getClass().getResource("/").toString());
            }
            // If we have a non-null url, then delegate the rest of the
            // configuration to the OptionConverter.selectAndConfigure
            // method.
            if (url != null) {
                LOG.debug("Using URL [" + url + "] for automatic spring configuration.");
                try {
                    resource = new UrlResource(url);
                } catch (NoClassDefFoundError e) {
                    LOG.warn("Error during default initialization", e);
                }
            } else {
                LOG.debug("Could not find resource: [" + beanConfigFile + "].");
            }

            beanFactory = new GenericApplicationContext();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);

            reader.loadBeanDefinitions(resource);

            URL configUrl = Loader.getResource(CONFIG_PROPERTIES);
            if (configUrl == null) {
                configUrl = this.getClass().getResource("/" + CONFIG_PROPERTIES);
            }

            if (configUrl != null) {
                PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
                LOG.debug("Use resource for bean properties: [" + configUrl + "].");
                cfg.setLocation(new UrlResource(configUrl));
                cfg.postProcessBeanFactory(beanFactory.getBeanFactory());
            } else {
                LOG.debug("Could not find resource: [" + CONFIG_PROPERTIES + "]");


            }
            beanFactory.refresh();
        }else {
            LOG.warn("beanFactory is initialized already, skip.");
        }
    }


    public synchronized String getBeanConfigFile() {
        return beanConfigFile;
    }

    public synchronized void setBeanConfigFile(String beanConfigFile) {
        this.beanConfigFile = beanConfigFile;
    }
}
