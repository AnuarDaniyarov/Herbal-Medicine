package kz.iitu.medicineplatform.auth;

import jakarta.validation.Valid;
import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService users;

    public AdminUserController(AdminUserService users) { this.users = users; }

    @GetMapping
    public PageResponse<AuthDtos.UserResponse> findAll(@PageableDefault(size = 25, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(users.findAll(pageable).map(AuthDtos.UserResponse::from));
    }

    @PatchMapping("/{id}/role")
    public AuthDtos.UserResponse updateRole(@PathVariable long id,
                                            @Valid @RequestBody AuthDtos.RoleUpdateRequest request) {
        return AuthDtos.UserResponse.from(users.updateRole(id, request.role()));
    }
}
