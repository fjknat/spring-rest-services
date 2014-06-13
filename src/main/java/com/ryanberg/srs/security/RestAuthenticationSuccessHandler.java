package com.ryanberg.srs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;

public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{

    private SecurityHeaderUtil headerUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException
    {
        try {
            String token = headerUtil.createAuthToken(((User) authentication.getPrincipal()).getUsername());
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode().put("token", token);
            PrintWriter out = response.getWriter();
            out.print(node.toString());
            out.flush();
            out.close();
        }
        catch (GeneralSecurityException e) {
            throw new ServletException("Unable to create the auth token", e);
        }
        clearAuthenticationAttributes(request);

    }

    public void setSecurityHeaderUtil(SecurityHeaderUtil headerUtil)
    {
        this.headerUtil = headerUtil;
    }
}