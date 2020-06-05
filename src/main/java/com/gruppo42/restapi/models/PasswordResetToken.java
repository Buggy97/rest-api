package com.gruppo42.restapi.models;

import javax.persistence.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;


@Entity
public class PasswordResetToken {

    public enum TokenStatus { INVALID, VALID, EXPIRED};

    private static final long EXPIRATIONMILLI = 24*60*60*1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public PasswordResetToken()
    {
        long expirationDate = Calendar.getInstance().getTimeInMillis()+EXPIRATIONMILLI;
        this.expiryDate = new Date();
        this.expiryDate.setTime(expirationDate);
    }

    public PasswordResetToken(String token, User user)
    {
        this.token = token;
        this.user = user;
        long expirationDate = Calendar.getInstance().getTimeInMillis()+EXPIRATIONMILLI;
        this.expiryDate = new Date();
        this.expiryDate.setTime(expirationDate);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }
}