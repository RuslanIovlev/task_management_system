package taskmanagementsystem.dto.auth;

public record RegistrationRequest(String email,
                                  String password,
                                  boolean isAdmin) {
}
