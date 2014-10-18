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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private DataSource dataSource;

    @Autowired
    private TokenUtils tokenHelper;

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception
    {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        auth
                .jdbcAuthentication().withDefaultSchema().dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .withUser("user").password(passwordEncoder.encode("password")).roles("USER").and()
                .withUser("admin").password(passwordEncoder.encode("password")).roles("USER", "ADMIN");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        RestAuthenticationSuccessHandler successHandler = new RestAuthenticationSuccessHandler();
        successHandler.setSecurityHeaderUtil(tokenHelper);

        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .csrf().disable()
                .apply(new RestLoginConfigurer<HttpSecurity>())
                .successHandler(successHandler).failureHandler(new RestAuthenticationFailureHandler())
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
                .antMatchers(HttpMethod.GET, "/greeting").permitAll()
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
        headerAuthenticationFilter.setTokenUtils(tokenHelper);
        return headerAuthenticationFilter;
    }
}
