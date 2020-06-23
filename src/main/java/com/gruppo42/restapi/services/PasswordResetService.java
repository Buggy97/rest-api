package com.gruppo42.restapi.services;

import com.gruppo42.restapi.models.PasswordResetToken;
import com.gruppo42.restapi.models.PasswordResetToken.TokenStatus;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.repository.PasswordTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Calendar;

@Service
public class PasswordResetService
{
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    private HTMLService htmlService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    public TokenStatus validateToken(String token)
    {
        final PasswordResetToken fetch = passwordTokenRepository.findByToken(token).orElse(null);
        if(fetch==null)
            return TokenStatus.INVALID;
        else if (fetch.getExpiryDate().before(Calendar.getInstance().getTime()))
            return TokenStatus.EXPIRED;
        else
            return TokenStatus.VALID;
    }

    @Transactional
    public void resetPasswordWithToken(User user, String token)
    {
        PasswordResetToken token1 = new PasswordResetToken(token, user);
        passwordTokenRepository.save(token1);
        sendResetTokenEmail(token, user);
    }

    private void sendResetTokenEmail(String token, User user)
    {
        String url = env.getProperty("app.url_to_use") + "/api/auth/changePassword?token=" + token;
        CharSequence htmlString = htmlService.getEmailFor(user.getName(), url);
        sendEmailWithAttachment("Resetta password", htmlString, user);
    }

    private void sendEmailWithAttachment(CharSequence subject, CharSequence body, User user)
    {
        try
        {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject.toString());
            helper.setText(body.toString(), true);
            javaMailSender.send(msg);
        } catch(Exception e)
        {
            logger.error("Exception while sending mail.");
            e.printStackTrace();
        }
    }
}
