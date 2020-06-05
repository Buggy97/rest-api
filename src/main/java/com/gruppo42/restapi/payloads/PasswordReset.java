package com.gruppo42.restapi.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordReset
{
    @NotBlank
    @Size(min = 6, max = 32)
    private String password1;

    @NotBlank
    @Size(min = 6, max = 32)
    private String passsword2;

    public PasswordReset()
    {
    }

    public PasswordReset(@NotBlank @Size(min = 6, max = 32) String password1, @NotBlank @Size(min = 6, max = 32) String passsword2)
    {
        this.password1 = password1;
        this.passsword2 = passsword2;
    }

    public String getPassword1()
    {
        return password1;
    }

    public void setPassword1(String password1)
    {
        this.password1 = password1;
    }

    public String getPasssword2()
    {
        return passsword2;
    }

    public void setPasssword2(String passsword2)
    {
        this.passsword2 = passsword2;
    }
}
