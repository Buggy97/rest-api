package com.gruppo42.experimental;

import com.gruppo42.restapi.services.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MailAttach
{
    final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    public void run() {
        //Email string interpolation
    }



    public static void main(String[] args) {
        MailAttach foo = new MailAttach();
        foo.run();
    }
}
