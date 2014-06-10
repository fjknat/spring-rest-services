package com.ryanberg.srs.security;

import com.google.common.base.Strings;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;

public class HeaderTokenBasedRememberMeService extends TokenBasedRememberMeServices
{
    private final String HEADER_SECURITY_TOKEN = "X-Auth-Token";

    public HeaderTokenBasedRememberMeService(String key, UserDetailsService userDetailsService)
    {
        super(key, userDetailsService);
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);

        if(Strings.isNullOrEmpty(token))
        {
            return null;
        }

        return token;
    }
}
