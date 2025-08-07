package com.bubbles.auth;

import com.bubbles.service.JwtService;
import com.bubbles.user.User;
import com.bubbles.user.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

record GoogleExchangeRequest(@Email String email, String level, String targetLanguage, @NotBlank String exchangeSecret) {}
record ExchangeResponse(String token) {}

@RestController
@RequestMapping("/api/auth")
public class AuthExchangeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String exchangeSecret;

    public AuthExchangeController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                                  @Value("${GOOGLE_EXCHANGE_SECRET:}") String exchangeSecret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.exchangeSecret = exchangeSecret;
    }

    @PostMapping("/google-exchange")
    public ResponseEntity<ExchangeResponse> exchange(@RequestBody GoogleExchangeRequest request) {
        if (exchangeSecret == null || exchangeSecret.isBlank() || !exchangeSecret.equals(request.exchangeSecret())) {
            return ResponseEntity.status(401).build();
        }
        var opt = userRepository.findByEmail(request.email());
        User user = opt.orElseGet(() -> {
            User u = new User();
            u.setEmail(request.email());
            u.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
            u.setLevel(request.level());
            u.setTargetLanguage(request.targetLanguage());
            return userRepository.save(u);
        });
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new ExchangeResponse(token));
    }
}


