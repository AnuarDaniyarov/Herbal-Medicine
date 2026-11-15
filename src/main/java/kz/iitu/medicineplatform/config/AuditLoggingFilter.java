package kz.iitu.medicineplatform.config;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalLatencyMs = new AtomicLong(0);
    private final AtomicLong maxLatencyMs = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> endpointCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> endpointLatency = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (path == null) {
            path = request.getRequestURI();
        }
        String key = method + " " + path;

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            int status = response.getStatus();

            totalRequests.incrementAndGet();
            totalLatencyMs.addAndGet(elapsed);
            maxLatencyMs.updateAndGet(max -> Math.max(max, elapsed));
            endpointCounts.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
            endpointLatency.computeIfAbsent(key, k -> new AtomicLong(0)).addAndGet(elapsed);

            // Log slow requests (>500ms)
            if (elapsed > 500) {
                log.warn("SLOW_REQUEST: {} {} status={} elapsed={}ms", method, path, status, elapsed);
            }

            // Log errors
            if (status >= 500) {
                log.error("ERROR_REQUEST: {} {} status={} elapsed={}ms", method, path, status, elapsed);
            }

            // Log security events
            if (status == 401 || status == 403) {
                log.warn("SECURITY_EVENT: {} {} status={} ip={}", method, path, status, getClientIp(request));
            }
        }
    }

    public long getTotalRequests() { return totalRequests.get(); }
    public long getTotalLatencyMs() { return totalLatencyMs.get(); }
    public long getMaxLatencyMs() { return maxLatencyMs.get(); }
    public long getAverageLatencyMs() {
        long total = totalRequests.get();
        return total > 0 ? totalLatencyMs.get() / total : 0;
    }
    public java.util.Map<String, Long> getEndpointCounts() {
        return endpointCounts.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, e -> e.getValue().get()));
    }
    public java.util.Map<String, Long> getEndpointLatency() {
        return endpointLatency.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, e -> e.getValue().get()));
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
}
