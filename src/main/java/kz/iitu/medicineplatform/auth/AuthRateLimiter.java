package kz.iitu.medicineplatform.auth;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import kz.iitu.medicineplatform.api.RateLimitExceededException;
import org.springframework.stereotype.Component;

@Component
public class AuthRateLimiter {

    private static final int MAX_LOGIN_FAILURES = 5;
    private static final long LOGIN_BLOCK_DURATION_SECONDS = 15 * 60; // 15 minutes

    private static final int MAX_REGISTRATIONS_PER_HOUR = 5;
    private static final long REGISTER_WINDOW_SECONDS = 60 * 60; // 1 hour

    private final Map<String, ConcurrentLinkedDeque<Instant>> loginFailures = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedDeque<Instant>> registrations = new ConcurrentHashMap<>();

    public void checkLogin(String email, String ip) {
        String key = (email == null ? "" : email.trim().toLowerCase()) + "|" + (ip == null ? "" : ip.trim());
        ConcurrentLinkedDeque<Instant> attempts = loginFailures.get(key);
        if (attempts != null) {
            cleanOldAttempts(attempts, LOGIN_BLOCK_DURATION_SECONDS);
            if (attempts.size() >= MAX_LOGIN_FAILURES) {
                throw limited("Too many failed login attempts.", attempts, LOGIN_BLOCK_DURATION_SECONDS);
            }
        }
    }

    public void recordLoginFailure(String email, String ip) {
        String key = (email == null ? "" : email.trim().toLowerCase()) + "|" + (ip == null ? "" : ip.trim());
        loginFailures.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>()).addLast(Instant.now());
    }

    public void recordLoginSuccess(String email, String ip) {
        String key = (email == null ? "" : email.trim().toLowerCase()) + "|" + (ip == null ? "" : ip.trim());
        loginFailures.remove(key);
    }

    public void checkRegister(String ip) {
        String key = ip == null ? "unknown" : ip.trim();
        ConcurrentLinkedDeque<Instant> attempts = registrations.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        cleanOldAttempts(attempts, REGISTER_WINDOW_SECONDS);
        if (attempts.size() >= MAX_REGISTRATIONS_PER_HOUR) {
            throw limited("Too many registration requests from this IP.", attempts, REGISTER_WINDOW_SECONDS);
        }
        attempts.addLast(Instant.now());
    }

    private void cleanOldAttempts(ConcurrentLinkedDeque<Instant> deque, long windowSeconds) {
        Instant cutoff = Instant.now().minusSeconds(windowSeconds);
        while (!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
            deque.pollFirst();
        }
    }

    private RateLimitExceededException limited(String reason, ConcurrentLinkedDeque<Instant> attempts,
                                               long windowSeconds) {
        Instant firstAttempt = attempts.peekFirst();
        long retryAfter = firstAttempt == null ? windowSeconds
                : Math.max(1, firstAttempt.plusSeconds(windowSeconds).getEpochSecond() - Instant.now().getEpochSecond());
        return new RateLimitExceededException(reason, retryAfter);
    }
}
