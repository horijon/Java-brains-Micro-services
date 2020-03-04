package com.kk.propertiesfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

// can override application.properties with another properties/yaml/json files
//@PropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "shared-application.properties")
@SpringBootApplication
public class PropertiesFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertiesFileApplication.class, args);
    }

}
