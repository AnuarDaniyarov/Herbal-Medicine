package kz.iitu.medicineplatform.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(UserRole role);
    @org.springframework.data.jpa.repository.Query("""
            select u.role, count(u)
            from UserAccount u
            group by u.role
            """)
    java.util.List<Object[]> countGroupedByRole();
}
