package com.ryanberg.srs.config;


import com.ryanberg.srs.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.servlet.Filter;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final String tokenKey = "5BD92B21-1AE5-444E-8C54-BA6DF24BFC28";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SecurityHeaderUtil headerUtil;

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        RestAuthenticationSuccessHandler successHandler = new RestAuthenticationSuccessHandler();
        successHandler.setSecurityHeaderUtil(headerUtil);

        http.addFilterBefore(authenticationFilter(), LogoutFilter.class)

                .csrf().disable()
                .formLogin().successHandler(successHandler).failureHandler(new RestAuthenticationFailureHandler())
                .loginProcessingUrl("/login")

                .and()

                .logout()
                .logoutSuccessUrl("/logout")

                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .exceptionHandling()
                .accessDeniedHandler(new RestAccessDeniedHandler())
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())

                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/logout").authenticated()
                .antMatchers(HttpMethod.GET, "/**").authenticated()
                .antMatchers(HttpMethod.POST, "/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/**").authenticated()
                .anyRequest().authenticated();

    }

    private Filter authenticationFilter()
    {
        HeaderTokenAuthenticationFilter headerAuthenticationFilter = new HeaderTokenAuthenticationFilter();
        headerAuthenticationFilter.setUserDetailsService(userDetailsService());
        headerAuthenticationFilter.setHeaderUtil(headerUtil);
        return headerAuthenticationFilter;
    }


}
