package com.financeTracker.financeTracker.config;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class VelocityConfig {

    @Bean
    public VelocityEngine velocityEngine() {
        Properties props = new Properties();

        // Load templates from classpath
        props.setProperty("resource.loaders", "class");
        props.setProperty(
                "class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        );

        props.setProperty("input.encoding", "UTF-8");
        props.setProperty("output.encoding", "UTF-8");

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(props);

        return velocityEngine;
    }
}

