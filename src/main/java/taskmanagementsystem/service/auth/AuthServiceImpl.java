package taskmanagementsystem.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanagementsystem.dto.auth.AuthenticationRequest;
import taskmanagementsystem.dto.auth.AuthenticationResponse;
import taskmanagementsystem.dto.auth.RegistrationRequest;
import taskmanagementsystem.entity.Role;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.jwt.JwtTokenProvider;
import taskmanagementsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public ResponseEntity<String> register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        Role role = request.isAdmin() ? Role.ADMIN : Role.USER;
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
