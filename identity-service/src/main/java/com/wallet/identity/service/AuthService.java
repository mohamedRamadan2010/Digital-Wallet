package com.wallet.identity.service;

import com.wallet.identity.dto.AuthRequest;
import com.wallet.identity.dto.AuthResponse;
import com.wallet.identity.dto.RegisterRequest;
import com.wallet.identity.entity.Role;
import com.wallet.identity.entity.User;
import com.wallet.identity.repository.UserRepository;
import com.wallet.identity.entity.TokenBlacklist;
import com.wallet.identity.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    public void validateToken(String token) {
        if (tokenBlacklistRepository.existsByToken(token)) {
            throw new RuntimeException("Token has been revoked");
        }
        jwtService.validateToken(token);
    }

    public void logout(String token) {
        // Remove Bearer prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Check if already blacklisted
        if (tokenBlacklistRepository.existsByToken(token)) {
            return; // Already logged out
        }

        // Extract expiration date from token
        LocalDateTime expiryDate = jwtService.extractExpiration(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Save to blacklist
        TokenBlacklist blacklist = TokenBlacklist.builder()
                .token(token)
                .expiryDate(expiryDate)
                .build();

        tokenBlacklistRepository.save(blacklist);
    }
}
