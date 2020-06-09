package com.gruppo42.restapi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HTMLService
{
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    private StringBuilder emailHTML;
    private StringBuilder passwordResetHTML;
    private  ClassLoader classLoader;

    public HTMLService()
    {
        this.classLoader = getClass().getClassLoader();
        this.emailHTML = new StringBuilder();
        this.passwordResetHTML = new StringBuilder();
        try {
            emailHTML.append(IOUtils.toString(classLoader.getResourceAsStream("templates/emailView/emailView.html"),
                                                StandardCharsets.UTF_8));
            passwordResetHTML.append(IOUtils.toString(classLoader.getResourceAsStream("templates/resetPasswordView/passwordResetView.html"),
                                                        StandardCharsets.UTF_8));
        } catch (Exception e)
        {
            logger.error("Error while loading templated HTMLs", e);
        }
    }

    @SuppressWarnings("ALL")
    public CharSequence getEmailFor(CharSequence username, CharSequence resetLink)
    {
        StringBuilder htmlString = new StringBuilder(emailHTML);
        Pattern pattern = Pattern.compile("@username");
        Matcher m = pattern.matcher(htmlString);
        while(m.find()) {
            htmlString.replace(m.start(), m.end(), username.toString());
        }
        //Replaces first occurence of @resetLink
        pattern = Pattern.compile("@resetLink");
        m = pattern.matcher(htmlString);
        while(m.find()) {
            htmlString.replace(m.start(), m.end(), resetLink.toString());
        }
        return htmlString;
    }

    public CharSequence getResetHTMLFor(CharSequence url)
    {
        StringBuilder htmlString = new StringBuilder(passwordResetHTML);
        Pattern pattern = Pattern.compile("@resetLink");
        Matcher m = pattern.matcher(htmlString);
        while(m.find()) {
            htmlString.replace(m.start(), m.end(), url.toString());
        }
        return htmlString;
    }
}
