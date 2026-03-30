import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterApp {

    // ================= TOKEN BUCKET =================
    static class TokenBucket {
        private final int maxTokens;
        private final double refillRate; // tokens per second
        private double tokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Refill tokens based on time elapsed
        private void refill() {
            long now = System.currentTimeMillis();
            double elapsedSeconds = (now - lastRefillTime) / 1000.0;

            double tokensToAdd = elapsedSeconds * refillRate;
            tokens = Math.min(maxTokens, tokens + tokensToAdd);

            lastRefillTime = now;
        }

        // Try consuming 1 token
        public synchronized boolean allowRequest() {
            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        public synchronized int getRemainingTokens() {
            refill();
            return (int) tokens;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public synchronized long getRetryAfterSeconds() {
            refill();
            if (tokens >= 1) return 0;

            double needed = 1 - tokens;
            return (long) Math.ceil(needed / refillRate);
        }
    }

    // ================= RATE LIMITER =================
    static class RateLimiter {

        private final ConcurrentHashMap<String, TokenBucket> clientBuckets;
        private final int MAX_REQUESTS = 1000;
        private final int WINDOW_SECONDS = 3600; // 1 hour

        public RateLimiter() {
            clientBuckets = new ConcurrentHashMap<>();
        }

        private TokenBucket getBucket(String clientId) {
            return clientBuckets.computeIfAbsent(clientId, id ->
                    new TokenBucket(MAX_REQUESTS, (double) MAX_REQUESTS / WINDOW_SECONDS)
            );
        }

        public String checkRateLimit(String clientId) {
            TokenBucket bucket = getBucket(clientId);

            if (bucket.allowRequest()) {
                return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
            } else {
                long retryAfter = bucket.getRetryAfterSeconds();
                return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
            }
        }

        public String getRateLimitStatus(String clientId) {
            TokenBucket bucket = getBucket(clientId);

            int remaining = bucket.getRemainingTokens();
            int used = bucket.getMaxTokens() - remaining;

            long currentTime = System.currentTimeMillis() / 1000;
            long resetTime = currentTime + bucket.getRetryAfterSeconds();

            return "{used: " + used + ", limit: " + bucket.getMaxTokens() +
                    ", reset: " + resetTime + "}";
        }
    }

    // ================= MAIN METHOD =================
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();
        String clientId = "abc123";

        // Simulate requests
        for (int i = 1; i <= 1005; i++) {
            String response = limiter.checkRateLimit(clientId);
            System.out.println("Request " + i + ": " + response);
        }

        // Check status
        System.out.println("\nFinal Status:");
        System.out.println(limiter.getRateLimitStatus(clientId));
    }
}