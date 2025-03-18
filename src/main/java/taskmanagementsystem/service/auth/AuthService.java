package taskmanagementsystem.service.auth;

import org.springframework.http.ResponseEntity;
import taskmanagementsystem.dto.auth.AuthenticationRequest;
import taskmanagementsystem.dto.auth.AuthenticationResponse;
import taskmanagementsystem.dto.auth.RegistrationRequest;

public interface AuthService {

    ResponseEntity<String> register(RegistrationRequest request);

    ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request);
}
