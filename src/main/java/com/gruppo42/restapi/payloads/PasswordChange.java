package com.gruppo42.restapi.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordChange
{
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 32)
    private String newPassword;

    public PasswordChange()
    {
    }

    public PasswordChange(@NotBlank String oldPassword, @NotBlank @Size(min = 6, max = 32) String newPassword)
    {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword()
    {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
}