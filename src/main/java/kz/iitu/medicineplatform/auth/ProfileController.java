package kz.iitu.medicineplatform.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public AuthDtos.UserResponse getProfile(@AuthenticationPrincipal UserAccount account) {
        return AuthDtos.UserResponse.from(account);
    }

    @PutMapping
    public AuthDtos.UserResponse updateProfile(@AuthenticationPrincipal UserAccount account,
                                                @Valid @RequestBody UpdateProfileRequest request) {
        account.setDisplayName(request.displayName());
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                throw new IllegalArgumentException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(request.currentPassword(), account.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            account.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }
        repository.save(account);
        return AuthDtos.UserResponse.from(account);
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 200) String displayName,
            String currentPassword,
            @Size(min = 12, max = 128) String newPassword) { }
}
