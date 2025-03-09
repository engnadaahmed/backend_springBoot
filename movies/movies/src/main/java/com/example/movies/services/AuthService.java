package com.example.movies.services;

import com.example.movies.Repo.RoleRepository;
import com.example.movies.Repo.UserRepository;
import com.example.movies.dto.LoginRequest;
import com.example.movies.dto.LoginResponse;
import com.example.movies.model.Role;
import com.example.movies.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public User signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign the default role (USER) to the new user
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        user.setRoles(Collections.singleton(defaultRole));

        return userRepository.save(user);
    }
    public User signUpForAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign the default role (USER) to the new user
        Role defaultRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        user.setRoles(Collections.singleton(defaultRole));

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate using email and password from the LoginRequest
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Load user details and generate a JWT token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        // Return the token wrapped in a LoginResponse object
        return new LoginResponse(token);
    }
    @Transactional
    // Ensures that the logout process, including any database changes, is handled within a single transaction
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        // Retrieves the current authentication information from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Checks if the user is authenticated (authentication is not null and authenticated)
        if (authentication != null && authentication.isAuthenticated()) {

            // Logs out the user by clearing their authentication info from the SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            // Gets the Authorization header from the request to retrieve the JWT token
            String token = request.getHeader("Authorization");

            // Checks if the token is in "Bearer <token>" format, then removes "Bearer " prefix to extract only the token value
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Extracts the actual token by removing the "Bearer " prefix
            }

        }
            // Returns a successful response indicating that the logout process was completed
        return ResponseEntity.ok("Logout successful");



    }
    }



