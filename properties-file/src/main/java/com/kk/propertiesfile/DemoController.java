package com.kk.propertiesfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DemoController {

    @Value("${my.greeting: default value}")
    private String greetingMessage;

    @Value("some static message")
    private String staticMessage;

    @Value("${my.list.values}")
    private List<String> listValues;

    @Value("#{${dbValues}}") // SpEL(Spring Expression Language)
    private Map<String, String> dbValues;

    @Autowired
    private DbSettings dbSettings;

    @Autowired
    private Environment environment;

    // type checking is done for both @Value and @ConfigurationProperties, if not matched then bean is not instantiated
    // throws error
    @GetMapping("/greeting")
    public String greeting() {
        return greetingMessage
                + System.lineSeparator()
                + staticMessage
                + System.lineSeparator()
                + listValues
                + System.lineSeparator()
                + dbValues
                + System.lineSeparator()
                + dbSettings.getConnection() + dbSettings.getHost() + dbSettings.getPort();
    }

    @GetMapping("/environmentDetails")
    public String environmentDetails() {
        return environment.toString();
    }

}
