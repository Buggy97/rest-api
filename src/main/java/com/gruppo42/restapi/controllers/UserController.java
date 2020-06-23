package com.gruppo42.restapi.controllers;

import com.gruppo42.restapi.execeptions.AppException;
import com.gruppo42.restapi.models.Movie;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.payloads.*;
import com.gruppo42.restapi.repository.MovieRepository;
import com.gruppo42.restapi.repository.UserRepository;
import com.gruppo42.restapi.security.CurrentUser;
import com.gruppo42.restapi.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api/user")
public class UserController
{
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser)
    {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(),
                                                currentUser.getName(), currentUser.getEmail(),
                                                currentUser.getImage(), currentUser.getFavorites(),
                                                currentUser.getWatchList());
        return userSummary;
    }

    @Transactional
    @PostMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse setCurrentUser(@RequestBody ProfileChangeRequest request,
                                      @CurrentUser UserPrincipal currentUser)
    {
        User user = userRepository.findById(currentUser.getId()).orElse(null);
        if(user==null)
            return new ApiResponse(false, "User not found");
        if(request.getEmail()!=null)
            user.setEmail(request.getEmail());
        if(request.getName()!=null)
            user.setName(request.getName());
        if(request.getImage()!=null)
            user.setPicByte(request.getImage());

        return new ApiResponse(true, "User profile changed successfully");
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/favorites")
    public Set<Movie> getFavorites(@CurrentUser UserPrincipal currentUser)
    {
        Set<Movie> ret = currentUser.getFavorites();
        return ret;
    }

    @GetMapping("/watchlist")
    public Set<Movie> getWatchList(@CurrentUser UserPrincipal currentUser)
    {
        Set<Movie> ret = currentUser.getWatchList();
        return ret;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add_favorite")
    @Transactional
    public ApiResponse addFavorite(@CurrentUser UserPrincipal currentUser,
                                   @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User not found."));
        Movie movie = getMovie(movie_id, true);
        user.getFavorites().add(movie);
        //No need to save, transactional does that automatically
        return new ApiResponse(true, "Movie " + movie_id + " added successfully to favorites.");
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add_watchlist")
    @Transactional
    public ApiResponse addWatchList(@CurrentUser UserPrincipal currentUser,
                                    @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User Role not set."));
        Movie movie = getMovie(movie_id, true);
        user.getWatchList().add(movie);
        //No need to save, transactional does that automatically
        return new ApiResponse(true, "Movie " + movie_id + " added successfully to watchlist.");
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/remove_favorite")
    @Transactional
    public ResponseEntity<?> removeFavorite(@CurrentUser UserPrincipal currentUser,
                                          @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User not found."));
        if(user.getFavorites().remove(getMovie(movie_id, false)))
            return new ResponseEntity(new ApiResponse(true, "Movie "+movie_id+" removed from favorite list."),
                    HttpStatus.OK);
        else
            return new ResponseEntity(new ApiResponse(false, "Movie "+movie_id+" not found."),
                    HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/remove_watchlist")
    @Transactional
    public ResponseEntity<?> removeWatchlist(@CurrentUser UserPrincipal currentUser,
                                             @RequestParam(value = "movie_id") String movie_id)
    {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException("User not found."));
        if(user.getWatchList().remove(getMovie(movie_id, false)))
            return new ResponseEntity(new ApiResponse(true, "Movie "+movie_id+" removed from watchlist."),
                    HttpStatus.OK);
        else
            return new ResponseEntity(new ApiResponse(false, "Movie "+movie_id+" not found."),
                    HttpStatus.NOT_FOUND);
    }


    private Movie getMovie(@RequestParam("movie_id") String movie_id, boolean save)
    {
        //Maybe add part to validate a movie_id
        Movie movie = movieRepository.findById(movie_id).orElse(null);
        if (movie == null)
        {
            movie = new Movie(movie_id);
            if(save)
                movieRepository.save(movie);
        }
        return movie;
    }

}