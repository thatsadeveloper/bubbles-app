package com.bubbles.auth;

import com.bubbles.service.JwtService;
import com.bubbles.user.User;
import com.bubbles.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record RegisterRequest(@Email String email, @NotBlank String password, String level, String nativeLanguage, String targetLanguage) {}
record LoginRequest(@Email String email, @NotBlank String password) {}
record ResendRequest(@Email String email) {}
record AuthResponse(String token) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setLevel(request.level());
        user.setNativeLanguage(request.nativeLanguage());
        user.setTargetLanguage(request.targetLanguage());
        userRepository.save(user);
        var vt = new VerificationToken();
        vt.setUserId(user.getId());
        vt.setToken(java.util.UUID.randomUUID().toString());
        vt.setExpiresAt(java.time.Instant.now().plusSeconds(60 * 60));
        verificationTokenRepository.save(vt);
        emailService.sendHtml(user.getEmail(), "Verify your email", emailService.buildVerificationEmailHtml(vt.getToken()));
        return ResponseEntity.ok(new AuthResponse("VERIFY_EMAIL"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@RequestParam("token") String token) {
        var opt = verificationTokenRepository.findByToken(token);
        if (opt.isEmpty()) return ResponseEntity.badRequest().build();
        var vt = opt.get();
        if (vt.isUsed() || vt.getExpiresAt().isBefore(java.time.Instant.now())) return ResponseEntity.status(410).build();
        vt.setUsed(true);
        verificationTokenRepository.save(vt);
        var user = userRepository.findById(vt.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@Valid @RequestBody ResendRequest request) {
        var optUser = userRepository.findByEmail(request.email());
        if (optUser.isEmpty()) return ResponseEntity.badRequest().build();
        var user = optUser.get();
        var vt = new VerificationToken();
        vt.setUserId(user.getId());
        vt.setToken(java.util.UUID.randomUUID().toString());
        vt.setExpiresAt(java.time.Instant.now().plusSeconds(60 * 60));
        verificationTokenRepository.save(vt);
        emailService.sendHtml(user.getEmail(), "Verify your email", emailService.buildVerificationEmailHtml(vt.getToken()));
        return ResponseEntity.ok().build();
    }
}


