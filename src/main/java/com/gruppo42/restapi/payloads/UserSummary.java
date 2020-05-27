package com.gruppo42.restapi.payloads;

import com.gruppo42.restapi.models.Movie;

import java.util.Set;

public class UserSummary
{
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private Set<Movie> favorites;
    private Set<Movie> watchList;

    public UserSummary(Long id, String username, String name, String surname, String email, Set<Movie> favorites, Set<Movie> watchList)
    {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.favorites = favorites;
        this.email = email;
        this.watchList = watchList;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public Set<Movie> getFavorites() { return favorites; }

    public void setFavorites(Set<Movie> favorites) { this.favorites = favorites; }

    public Set<Movie> getWatchList() { return watchList; }

    public void setWatchList(Set<Movie> watchList) { this.watchList = watchList; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
