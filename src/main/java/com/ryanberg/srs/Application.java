package com.ryanberg.srs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan
@PropertySource({"classpath:config.properties"})
@EnableAutoConfiguration
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
