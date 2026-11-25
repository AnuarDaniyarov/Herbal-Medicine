package kz.iitu.medicineplatform.auth;

import kz.iitu.medicineplatform.api.RateLimitExceededException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthRateLimiterTest {
    @Test
    void blocksSixthLoginAttemptAndSuppliesRetryAfter() {
        AuthRateLimiter limiter = new AuthRateLimiter();
        for (int attempt = 0; attempt < 5; attempt++) {
            limiter.recordLoginFailure("user@example.test", "192.0.2.1");
        }

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class,
                () -> limiter.checkLogin("user@example.test", "192.0.2.1"));

        assertTrue(exception.getRetryAfterSeconds() > 0);
        assertEquals(429, exception.getStatusCode().value());
    }

    @Test
    void blocksSixthRegistrationRequestFromSameIp() {
        AuthRateLimiter limiter = new AuthRateLimiter();
        for (int attempt = 0; attempt < 5; attempt++) {
            limiter.checkRegister("192.0.2.2");
        }

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class,
                () -> limiter.checkRegister("192.0.2.2"));

        assertTrue(exception.getRetryAfterSeconds() > 0);
    }
}
