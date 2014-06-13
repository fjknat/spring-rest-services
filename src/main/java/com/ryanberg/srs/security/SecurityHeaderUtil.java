package com.ryanberg.srs.security;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class SecurityHeaderUtil
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityHeaderUtil.class);

    private final String HEADER_SECURITY_TOKEN = "X-Auth-Token";

    private EncryptionUtil encryptionUtil = new EncryptionUtil();
    private Period sessionMaxAge;
    private String seed;

    @Autowired
    private Environment environment;

    @PostConstruct
    private void init()
    {
        String encryptionEnabled = environment.getProperty("auth.encryption.enabled");
        if (!Strings.isNullOrEmpty(encryptionEnabled) && Boolean.parseBoolean(encryptionEnabled)) {
            encryptionUtil.encryptionEnabled(true);
            seed = environment.getRequiredProperty("auth.encryption.seed");
        }
        sessionMaxAge = getSessionMaxAge();
    }

    public String getUserName(HttpServletRequest request)
    {
        String header = request.getHeader(HEADER_SECURITY_TOKEN);
        return Strings.isNullOrEmpty(header) ? null : extractUserName(header);
    }

    private String extractUserName(String value)
    {

        try {
            String decryptedValue = encryptionUtil.decrypt(value, seed);
            String[] split = decryptedValue.split("\\|");
            String username = split[0];
            DateTime timestamp = new DateTime(Long.parseLong(split[1]));
            if (timestamp.isAfter(DateTime.now().minus(sessionMaxAge))) {
                return username;
            }
        }
        catch (IOException | GeneralSecurityException e) {
            logger.debug("Unable to decrypt header", e);
        }
        return null;
    }

    public void addHeader(HttpServletResponse response, String userName)
    {
        try {
            String encryptedValue = createAuthToken(userName);
            response.setHeader(HEADER_SECURITY_TOKEN, encryptedValue);
        }
        catch (IOException | GeneralSecurityException e) {
            logger.error("Unable to encrypt header", e);
        }
    }

    public String createAuthToken(String userName) throws IOException, GeneralSecurityException
    {
        String value = userName + "|" + System.currentTimeMillis();
        return encryptionUtil.encrypt(value, seed);
    }

    private Period getSessionMaxAge()
    {
        String maxAge = environment.getRequiredProperty("auth.session.maxAge");
        PeriodFormatter format = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d", "d")
                .printZeroRarelyFirst()
                .appendHours()
                .appendSuffix("h", "h")
                .printZeroRarelyFirst()
                .appendMinutes()
                .appendSuffix("m", "m")
                .toFormatter();
        Period sessionMaxAge = format.parsePeriod(maxAge);

        logger.debug("Session maxAge is: {}",
                formatIfNotZero(sessionMaxAge.getDays(), "days", "day") +
                        formatIfNotZero(sessionMaxAge.getHours(), "hours", "hour") +
                        formatIfNotZero(sessionMaxAge.getMinutes(), "minutes", "minute")
        );

        return sessionMaxAge;
    }

    private static String formatIfNotZero(int value, String plural, String singleton)
    {
        if (value > 0) {
            if (value > 1) {
                return "" + value + " " + plural;
            }
            return "" + value + " " + singleton;
        }
        return "";
    }
}
