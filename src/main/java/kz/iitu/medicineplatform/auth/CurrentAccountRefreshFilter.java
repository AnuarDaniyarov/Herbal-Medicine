package kz.iitu.medicineplatform.auth;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

public class CurrentAccountRefreshFilter extends OncePerRequestFilter {
    private final UserAccountRepository users;
    private final HttpSessionSecurityContextRepository sessions = new HttpSessionSecurityContextRepository();

    public CurrentAccountRefreshFilter(UserAccountRepository users) { this.users = users; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserAccount principal) {
            UserAccount current = users.findById(principal.getId()).orElse(null);
            if (current == null || !current.isEnabled()) {
                SecurityContextHolder.clearContext();
                if (request.getSession(false) != null) request.getSession(false).invalidate();
            } else if (current.getRole() != principal.getRole()) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        current, null, current.getAuthorities()));
                SecurityContextHolder.setContext(context);
                sessions.saveContext(context, request, response);
            }
        }
        chain.doFilter(request, response);
    }
}
