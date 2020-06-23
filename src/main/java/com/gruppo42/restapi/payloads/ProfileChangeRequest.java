package com.gruppo42.restapi.payloads;

public class ProfileChangeRequest
{
    private String username;
    private String name;
    private String email;
    private String image;


    public ProfileChangeRequest(String username, String name, String email, String image)
    {
        this.username = username;
        this.name = name;
        this.image = image;
        this.email = email;

    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }
}
