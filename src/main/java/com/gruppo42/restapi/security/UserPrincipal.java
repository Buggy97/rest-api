package com.gruppo42.restapi.security;

import com.gruppo42.restapi.models.Movie;
import com.gruppo42.restapi.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails
{
    private Long id;

    private String name;

    private String surname;

    private String username;

    @JsonIgnore
    private Set<Movie> watchList;

    @JsonIgnore
    private Set<Movie> favorites;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String name, String surname,
                         String username, String email, String password,
                         Set<Movie> favorites,Set<Movie> watchList,
                         Collection<? extends GrantedAuthority> authorities)
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        /*
        The copy is needed here because it forces jpa to load the sets and copy
        them to the user principal. This avoids the lazyInitialization exception
        since the UserPrincipal gets created whenever a user gets loaded.
        The possible downside is that for every request it needs also to load the sets
        which can be quite demanding if the sets are big. For demo purposes i will leave
        it as it is. An optimization could be creating a specific service to load sets given
        user id.
         */
        this.favorites = new HashSet<>(favorites);
        this.watchList = new HashSet<>(watchList);
    }

    public static UserPrincipal create(User user)
    {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFavorites(),
                user.getWatchList(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() { return surname; }

    public String getEmail() {
        return email;
    }

    public Set<Movie> getFavorites() { return favorites; }

    public Set<Movie> getWatchList() { return watchList; }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }


}