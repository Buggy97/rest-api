package com.gruppo42.restapi.controllers;

import com.gruppo42.restapi.execeptions.AppException;
import com.gruppo42.restapi.models.Movie;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.payloads.ApiResponse;
import com.gruppo42.restapi.payloads.UserIdentityAvailability;
import com.gruppo42.restapi.payloads.UserSummary;
import com.gruppo42.restapi.repository.MovieRepository;
import com.gruppo42.restapi.repository.UserRepository;
import com.gruppo42.restapi.security.CurrentUser;
import com.gruppo42.restapi.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api")
public class UserController
{
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser)
    {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(),
                                                currentUser.getName(), currentUser.getSurname(),
                                                currentUser.getEmail(), currentUser.getFavorites(),
                                                currentUser.getWatchList());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/favorites")
    public Set<Movie> getFavorites(@CurrentUser UserPrincipal currentUser)
    {
        Set<Movie> ret = currentUser.getFavorites();
        return ret;
    }

    @GetMapping("/user/watchlist")
    public Set<Movie> getWatchList(@CurrentUser UserPrincipal currentUser)
    {
        Set<Movie> ret = currentUser.getWatchList();
        return ret;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/user/add_favorite")
    @Transactional
    public ApiResponse addFavorite(@CurrentUser UserPrincipal currentUser,
                                   @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User not found."));
        Movie movie = getMovie(movie_id);
        user.getFavorites().add(movie);
        //No need to save, transactional does that automatically
        return new ApiResponse(true, "Movie " + movie_id + " added successfully to favorites.");
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/user/add_watchlist")
    @Transactional
    public ApiResponse addWatchList(@CurrentUser UserPrincipal currentUser,
                                    @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User Role not set."));
        Movie movie = getMovie(movie_id);
        user.getWatchList().add(movie);
        //No need to save, transactional does that automatically
        return new ApiResponse(true, "Movie " + movie_id + " added successfully to watchlist.");
    }

    private Movie getMovie(@RequestParam("movie_id") String movie_id)
    {
        //Maybe add part to validate a movie_id
        Movie movie = movieRepository.findById(movie_id).orElse(null);
        if (movie == null)
        {
            movie = new Movie(movie_id);
            movieRepository.save(movie);
        }
        return movie;
    }

}