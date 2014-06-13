package com.ryanberg.srs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: rberg
 * Date: 6/12/14
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
    {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode().put("error", "true").put("message", "Authentication failed.").put("cause", "BAD_USERNAME_PASSWORD");
            PrintWriter out = response.getWriter();
            out.print(node.toString());
            out.flush();
            out.close();
        }
        catch (Exception e) {
            throw new ServletException("Unable to create the auth token", e);
        }

    }
}
