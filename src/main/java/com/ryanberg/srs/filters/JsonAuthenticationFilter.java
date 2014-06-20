package com.ryanberg.srs.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom authentication filter to parse JSON credentials
 */
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private static final Logger logger = LoggerFactory.getLogger(JsonAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest = getUsernamePasswordToken(request);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordToken(HttpServletRequest request)
    {
        UsernamePasswordAuthenticationToken token = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonObj = mapper.readTree(request.getInputStream());
            String username = jsonObj.get("username").asText();
            String password = jsonObj.get("password").asText();
            token = new UsernamePasswordAuthenticationToken(username, password);
        }
        catch (Exception e) {
            logger.error("Exception while parsing JSON credentials", e);
        }

        return token;
    }
}
