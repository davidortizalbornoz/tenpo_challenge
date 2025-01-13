package cl.tempo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    @Value("${app.rate.limit.requests}")
    private int maxRequests;

    @Value("${app.rate.limit.per.minutes}")
    private int perMinutes;

    private final Map<String, RequestCount> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = httpRequest.getRemoteAddr();

        RequestCount count = requestCounts.computeIfAbsent(clientIp, k -> new RequestCount());

        if (count.isAllowed()) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Rate limit exceeded. Try again later.");
        }
    }

    private class RequestCount {
        private int count = 0;
        private long lastResetTime = System.currentTimeMillis();

        public synchronized boolean isAllowed() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastResetTime > perMinutes * 60 * 1000) {
                count = 0;
                lastResetTime = currentTime;
            }

            if (count < maxRequests) {
                count++;
                return true;
            }
            return false;
        }
    }
}