package kz.iitu.medicineplatform.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Simple in-memory rate limiter that tracks requests per IP address.
 * Limits to 100 requests per 60-second window per IP.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip rate limiting for health checks and static resources
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        long now = System.currentTimeMillis();

        RequestCounter counter = counters.computeIfAbsent(ip, k -> new RequestCounter());
        synchronized (counter) {
            if (now - counter.windowStart > WINDOW_MS) {
                counter.windowStart = now;
                counter.count = 1;
            } else {
                counter.count++;
            }
        }

        int currentCount;
        synchronized (counter) {
            currentCount = counter.count;
        }

        if (currentCount > MAX_REQUESTS) {
            log.warn("RATE_LIMIT_EXCEEDED: ip={} count={}", ip, currentCount);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"Please try again later\"}");
            return;
        }

        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(MAX_REQUESTS - currentCount));
        response.setHeader("X-RateLimit-Reset", String.valueOf(counter.windowStart + WINDOW_MS));

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static class RequestCounter {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }
}
