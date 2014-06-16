package com.ryanberg.srs.config;

import com.ryanberg.srs.filters.JsonAuthenticationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestLoginConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, RestLoginConfigurer<H>, JsonAuthenticationFilter>
{

    public RestLoginConfigurer()
    {
        super(new JsonAuthenticationFilter(), null);
        usernameParameter("username");
        passwordParameter("password");
    }

    public RestLoginConfigurer<H> usernameParameter(String usernameParameter)
    {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    public RestLoginConfigurer<H> passwordParameter(String passwordParameter)
    {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl)
    {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
