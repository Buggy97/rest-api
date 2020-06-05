package com.gruppo42.restapi.services;

import com.gruppo42.restapi.models.PasswordResetToken;
import com.gruppo42.restapi.models.User;
import com.gruppo42.restapi.repository.PasswordTokenRepository;
import com.gruppo42.restapi.repository.UserRepository;
import com.gruppo42.restapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service used by authentication managers to find users given mail, id or username
 * it is important to define this since the authentication manager alone can not
 * determine where to find a user. It returns a UserDetails class once it finds
 * the specified user.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService
{
    /*
    One important thing about @Transactional is that it creates a session and ends it
    when the method is executed, every operation after a transaction
    can't be executed. Lazy fetchtypes thus require attention: if we try to
    retrieve a lazily loaded member, it will cause errors.
     */

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException
    {
        // Let people login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
                );

        UserPrincipal ret = UserPrincipal.create(user);
        return ret;
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        UserPrincipal ret = UserPrincipal.create(user);
        return ret;
    }

}