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
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final String tokenKey = "5BD92B21-1AE5-444E-8C54-BA6DF24BFC28";

    @Autowired
    public void registerGlobalAuthentication(
            AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN");

        auth.authenticationProvider(rememberMeAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint()).and()
                .formLogin().permitAll().and()
                .rememberMe().rememberMeServices(tokenBasedRememberMeService());
    }

    @Bean public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception{
        return new RememberMeAuthenticationFilter(authenticationManager(), tokenBasedRememberMeService());
    }

    @Bean
    public HeaderTokenBasedRememberMeService tokenBasedRememberMeService(){
        HeaderTokenBasedRememberMeService service = new HeaderTokenBasedRememberMeService(tokenKey, userDetailsService());
        service.setAlwaysRemember(true);
        service.setCookieName("X-Auth-Token");
        return service;
    }

    @Bean
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider(){
        return new RememberMeAuthenticationProvider(tokenKey);
    }
}
