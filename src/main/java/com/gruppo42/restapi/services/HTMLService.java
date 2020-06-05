package com.gruppo42.restapi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        File emailView = new File(classLoader.getResource("templates/emailView/emailView.html").getFile());
        File passwordView = new File(classLoader.getResource("templates/resetPasswordView/passwordResetView.html").getFile());
        try (Stream<String> stream = Files.lines(emailView.toPath(), StandardCharsets.UTF_8);
             Stream<String> stream2 = Files.lines(passwordView.toPath(), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> this.emailHTML.append(s).append("\n"));
            stream2.forEach(s ->this.passwordResetHTML.append(s).append("\n"));
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
        try (FileWriter fw = new FileWriter("C:\\Users\\Farjad\\Desktop\\uni\\output.html"))
        {
            logger.info("Printing file");
            fw.append(htmlString);
            fw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
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
