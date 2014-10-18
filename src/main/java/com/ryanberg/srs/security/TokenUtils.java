package com.ryanberg.srs.security;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

@Component
public class TokenUtils {
    private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    private final String HEADER_SECURITY_TOKEN = "X-Auth-Token";

    private KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();

    @Autowired
    private Environment environment;

    @PostConstruct
    private void init() {
        tokenService.setServerInteger(environment.getRequiredProperty("auth.server.integer", Integer.class));
        tokenService.setServerSecret(environment.getRequiredProperty("auth.server.secret"));
        tokenService.setPseudoRandomNumberBytes(environment.getRequiredProperty("auth.token.length", Integer.class));
        tokenService.setSecureRandom(new SecureRandom());
    }

    public String getUserName(HttpServletRequest request) {
        String header = request.getHeader(HEADER_SECURITY_TOKEN);
        return Strings.isNullOrEmpty(header) ? null : extractUserName(header);
    }

    private String extractUserName(String value) {
        Token token = tokenService.verifyToken(value);
        return token.getExtendedInformation();
    }

    public void addHeader(HttpServletResponse response, String username) {
        try {
            String encryptedValue = createAuthToken(username);
            response.setHeader(HEADER_SECURITY_TOKEN, encryptedValue);
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Unable to generate header auth token", e);
        }
    }

    public String createAuthToken(String username) throws IOException, GeneralSecurityException {
        Token token = tokenService.allocateToken(username);
        return token.getKey();
    }
}
