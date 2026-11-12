package kz.iitu.medicineplatform.auth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminUserService {
    private final UserAccountRepository users;

    public AdminUserService(UserAccountRepository users) { this.users = users; }

    @Transactional(readOnly = true)
    public Page<UserAccount> findAll(Pageable pageable) { return users.findAll(pageable); }

    @Transactional
    public UserAccount updateRole(long id, UserRole newRole) {
        UserAccount account = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (account.getRole() == UserRole.ADMIN && newRole != UserRole.ADMIN
                && users.countByRole(UserRole.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The last administrator cannot be demoted");
        }
        account.changeRole(newRole);
        return account;
    }
}
