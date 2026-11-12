package kz.iitu.medicineplatform.auth;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminBootstrapRunner(UserAccountRepository users, PasswordEncoder passwordEncoder,
                                @Value("${app.security.bootstrap-admin-email:}") String email,
                                @Value("${app.security.bootstrap-admin-password:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) return;
        if (email.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Set both BOOTSTRAP_ADMIN_EMAIL and a BOOTSTRAP_ADMIN_PASSWORD of at least 12 characters");
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (!users.existsByEmailIgnoreCase(normalizedEmail)) {
            users.save(new UserAccount(normalizedEmail, "Platform administrator",
                    passwordEncoder.encode(password), UserRole.ADMIN));
        }
    }
}
