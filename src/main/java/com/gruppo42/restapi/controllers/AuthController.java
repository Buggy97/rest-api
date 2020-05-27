package com.gruppo42.restapi.controllers;

import com.gruppo42.restapi.execeptions.AppException;
import com.gruppo42.restapi.models.Role;
import com.gruppo42.restapi.models.RoleName;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.payloads.ApiResponse;
import com.gruppo42.restapi.payloads.JwtAuthenticationResponse;
import com.gruppo42.restapi.payloads.LoginRequest;
import com.gruppo42.restapi.payloads.SignUpRequest;
import com.gruppo42.restapi.repository.RoleRepository;
import com.gruppo42.restapi.repository.UserRepository;
import com.gruppo42.restapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;


@RestController
@RequestMapping("/api/auth")
/**Main class for the API.
 * Every other classes defined are made to be used by this one.
 *
 */
public class AuthController {

    /**
     * Authentication manager.
     * Defined already by spring, it allows to authenticate using email/username and
     * password.
     */
    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * Interface for user persistence.
     * This needs to be created if we want to save users on DB.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Interface for role persistence.
     * This needs to be created if we want to save user roles on DB.
     */
    @Autowired
    RoleRepository roleRepository;

    /**
     * Encoder for passwords.
     * Essential if we want to store passwords on DB, it hashes the password with a randomly
     * generated salt for better security
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * JWT token provider
     * Generated Bearer tokens to allow user authentication and authorization without making them
     * login for every single operation
     */
    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {

        /*
        Authentication contains user information as a specified class (UserPrincipal)
        and allows to subsequently generate tokens given the information.
         */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        /*
        Sets the authentication in the context so every method can use it if needed
         */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
        Generates the token given the authentication and returns it to the user
         */
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest)
    {

        if(userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail()))
        {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getSurname(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        //The password is stored in a secure way and not in plain
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        //Returns a URI for the new resource created (the user)
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}