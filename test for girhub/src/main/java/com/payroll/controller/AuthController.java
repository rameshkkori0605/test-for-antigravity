package com.payroll.controller;

import com.payroll.dto.AuthResponse;
import com.payroll.dto.LoginRequest;
import com.payroll.model.User;
import com.payroll.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Correct Spring Security 6 session context persistence
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            String fullName = userOpt.map(User::getFullName).orElse("Administrator");
            String role = userOpt.map(User::getRole).orElse("ROLE_ADMIN");

            AuthResponse authResponse = new AuthResponse(
                    true,
                    loginRequest.getEmail(),
                    fullName,
                    role,
                    "Login successful"
            );
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(
                    false,
                    loginRequest.getEmail(),
                    null,
                    null,
                    "Invalid email or password"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return ResponseEntity.ok(new AuthResponse(false, null, null, null, "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        Authentication auth = securityContextHolderStrategy.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            Optional<User> userOpt = userRepository.findByEmail(auth.getName());
            return ResponseEntity.ok(new AuthResponse(
                    true,
                    auth.getName(),
                    userOpt.map(User::getFullName).orElse(auth.getName()),
                    userOpt.map(User::getRole).orElse("ROLE_ADMIN"),
                    "User authenticated"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, null, null, null, "Not authenticated"));
    }
}
