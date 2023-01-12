package com.example.es;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

/**
 * 参考 官网
 * 1.  https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.3/java-rest-high-getting-started-initialization.html
 * 参考 使用案例
 * 2.  https://blog.csdn.net/m0_37845840/article/details/103856827
 * @author xwx
 */

@Configuration
public class EsConfig {

    @Bean(name = "businessEsProperties")
    @ConfigurationProperties(prefix="elasticsearch.business")
    public Properties businessEsProperties(){
        return new Properties();
    }

    @Bean(name = "contentEs")
    public Map<String, String> contentEs(@Qualifier("businessEsProperties") Properties prop){
        Map<String, String> map = Maps.newHashMap();
        map.put("host", prop.getProperty("host"));
        map.put("password", prop.getProperty("password"));
        map.put("username", prop.getProperty("username"));
        map.put("version","7.1.0");
        return map;
    }

}
