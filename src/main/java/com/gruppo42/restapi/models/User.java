package com.gruppo42.restapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gruppo42.restapi.audit.DateAudit;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "lastLogin"},
                      allowGetters = true)
public class User extends DateAudit
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @NaturalId(mutable=true)
    @NotBlank
    @Email
    @Size(max = 40)
    private String email;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    private String password;

    @Lob
    @Column(name = "picByte", length=2147483647)
    private String picByte;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> favorites = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_watchlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> watchList = new HashSet<>();

    public User()
    {
    }

    public User(String name, String username, String email, String password, String image)
    {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.picByte = image;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Set<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<Role> roles)
    {
        this.roles = roles;
    }

    public String getPicByte() { return picByte; }

    public void setPicByte(String picByte) { this.picByte = picByte; }

    public Set<Movie> getFavorites() { return favorites; }

    public void setFavorites(Set<Movie> favorites) { this.favorites = favorites; }

    public Set<Movie> getWatchList() { return watchList; }

    public void setWatchList(Set<Movie> watchList) { this.watchList = watchList; }
}