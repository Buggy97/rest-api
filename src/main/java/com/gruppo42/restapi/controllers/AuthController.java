package com.gruppo42.restapi.controllers;

import com.gruppo42.restapi.execeptions.AppException;
import com.gruppo42.restapi.execeptions.BadRequestException;
import com.gruppo42.restapi.models.PasswordResetToken;
import com.gruppo42.restapi.models.PasswordResetToken.TokenStatus;
import com.gruppo42.restapi.models.Role;
import com.gruppo42.restapi.models.RoleName;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.payloads.*;
import com.gruppo42.restapi.repository.PasswordTokenRepository;
import com.gruppo42.restapi.repository.RoleRepository;
import com.gruppo42.restapi.repository.UserRepository;
import com.gruppo42.restapi.security.CurrentUser;
import com.gruppo42.restapi.security.JwtTokenProvider;
import com.gruppo42.restapi.security.UserPrincipal;
import com.gruppo42.restapi.services.HTMLService;
import com.gruppo42.restapi.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
/**Main class for the API.
 * Every other classes defined are made to be used by this one.
 *
 */
public class AuthController
{
    /**
     * For application.properties
     */
    @Autowired
    private Environment env;

    /**
     * HTML service.
     * Retrieves templated html pages and returns them filled with custom specified data
     */
    @Autowired
    HTMLService htmlService;

    /**
     * Password token repository
     */
    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    /**
     * Password reset service.
     * Resets password.
     */
    @Autowired
    PasswordResetService passwordResetService;

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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/newPassword")
    @Transactional
    public ApiResponse newPassword(@CurrentUser UserPrincipal currentUser,
                                   @Valid @RequestBody PasswordChange passwordChange)
    {
        ApiResponse response = null;
        User user = userRepository.findById(currentUser.getId())
                                .orElseThrow(()->{return new AppException("User not found");});
        String passHash = passwordEncoder.encode(passwordChange.getOldPassword());
        if(passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword()))
        {
            user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
            userRepository.save(user);
            response = new ApiResponse(true, "password changed");
        }
        else
            response = new ApiResponse(false, "Password does not match");
        return response;
    }

    @GetMapping("/changePassword")
    @Transactional
    public String changePassword(@RequestParam(value = "token") String token, HttpServletResponse response)
    {
        TokenStatus status = passwordResetService.validateToken(token);
        if(status==TokenStatus.INVALID)
            throw new BadRequestException("Invalid token");
        else if(status== TokenStatus.EXPIRED)
            throw new BadRequestException("Expired token");
        else
        {
            String url = "/api/auth/confirmPassword";
            Cookie cookie = new Cookie("quarantadue", token);
            //cookie.setHttpOnly(true);
            //cookie.setSecure(true);
            response.addCookie(cookie);
            return htmlService.getResetHTMLFor(url).toString();
        }
    }

    @PostMapping("/confirmPassword")
    @Transactional
    public ApiResponse confirmPassword(HttpServletRequest request)
    {
        PasswordReset passwordReset = new PasswordReset(request.getParameter("password1"), request.getParameter("password2"));
        String token = request.getCookies()[0].getValue();
        ApiResponse apiResponse = new ApiResponse(true, "Password changed.");
        TokenStatus status = passwordResetService.validateToken(token);
        System.out.println(token);
        if(!passwordReset.getPassword1().equals(passwordReset.getPasssword2()))
            throw  new BadRequestException("Password do not match");
        if(status==TokenStatus.INVALID)
            throw new BadRequestException("Invalid token");
        else if(status== TokenStatus.EXPIRED)
            throw new BadRequestException("Expired token");
        else
        {
            PasswordResetToken tokenFetch = passwordTokenRepository.findByToken(token).orElseThrow(() ->{
                    return new BadRequestException("Invalid token");
            });
            User user = userRepository.findById(tokenFetch.getUser().getId()).orElseThrow(() ->{
                return new BadRequestException("User not found");
            });
            System.out.println("Saving passs: " + passwordReset.getPassword1());
            user.setPassword(passwordEncoder.encode(passwordReset.getPassword1()));
            PasswordResetToken resetToken = passwordTokenRepository.findByToken(token).orElse(null);
            passwordTokenRepository.delete(resetToken);
            System.out.println("***********SAVING***********");
            return apiResponse;
        }
    }

    @GetMapping("/resetPassword")
    @Transactional
    public ApiResponse resetPassword(@RequestParam(value = "email") String email)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found."));
        String token = UUID.randomUUID().toString();
        passwordResetService.resetPasswordWithToken(user, token);
        //IMPORTANT REMOVE BEFORE RELEASE
        System.out.println("Sent token: {"+token+"}");
        ApiResponse response = new ApiResponse(true, "An email has been sent to " + email);
        return response;
    }

}