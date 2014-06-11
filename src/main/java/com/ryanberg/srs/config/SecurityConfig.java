package com.ryanberg.srs.config;


import com.ryanberg.srs.security.HeaderTokenBasedRememberMeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final String tokenKey = "5BD92B21-1AE5-444E-8C54-BA6DF24BFC28";

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN");

        auth.authenticationProvider(rememberMeAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        // disable csrf
        http.csrf().disable();
        // apply security to all http requests
        http.authorizeRequests().anyRequest().authenticated();
        // do not use sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // do not redirect to login url, instead return 403 Forbidden
        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
        // allow access to POST to login endpoint
        http.formLogin().permitAll();
        // use remember me cookie
        http.rememberMe().rememberMeServices(tokenBasedRememberMeService());
    }

    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception
    {
        return new RememberMeAuthenticationFilter(authenticationManager(), tokenBasedRememberMeService());
    }

    @Bean
    public HeaderTokenBasedRememberMeService tokenBasedRememberMeService()
    {
        HeaderTokenBasedRememberMeService service = new HeaderTokenBasedRememberMeService(tokenKey, userDetailsService());
        service.setAlwaysRemember(true);
        service.setCookieName("X-Auth-Token");
        return service;
    }

    @Bean
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider()
    {
        return new RememberMeAuthenticationProvider(tokenKey);
    }
}
