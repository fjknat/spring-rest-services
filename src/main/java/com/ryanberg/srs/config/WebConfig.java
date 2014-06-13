package com.ryanberg.srs.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebConfig
{
    @Bean
    public FilterRegistrationBean encodingFilter()
    {
        FilterRegistrationBean frb = new FilterRegistrationBean();
        frb.setFilter(new CharacterEncodingFilter());

        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("encoding", "UTF-8");
        initParameters.put("forceEncoding", "true");

        frb.setInitParameters(initParameters);

        return frb;
    }
}
