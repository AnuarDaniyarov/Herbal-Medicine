package kz.iitu.medicineplatform.auth;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() { }

    public record RegisterRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(min = 2, max = 200) String displayName,
            @NotBlank @Size(min = 12, max = 128) String password) { }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(max = 128) String password) { }

    public record RoleUpdateRequest(@NotNull UserRole role) { }

    public record UserResponse(Long id, String email, String displayName, UserRole role, OffsetDateTime createdAt) {
        public static UserResponse from(UserAccount account) {
            return new UserResponse(account.getId(), account.getEmail(), account.getDisplayName(),
                    account.getRole(), account.getCreatedAt());
        }
    }
}
