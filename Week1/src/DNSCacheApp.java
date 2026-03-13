import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int MAX_SIZE;

    private LinkedHashMap<String, DNSEntry> cache;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public DNSCache(int size) {
        this.MAX_SIZE = size;

        cache = new LinkedHashMap<String, DNSEntry>(size, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_SIZE; // LRU eviction
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public String resolve(String domain) {

        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                cacheHits++;
                long endTime = System.nanoTime();

                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (" + ((endTime - startTime) / 1_000_000.0) + " ms)");

                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED for " + domain);
            }
        }

        cacheMisses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 10)); // TTL = 10 seconds

        long endTime = System.nanoTime();

        System.out.println("Cache MISS → Query upstream → " + ip +
                " (" + ((endTime - startTime) / 1_000_000.0) + " ms)");

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate network delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }

    // Background thread to remove expired entries
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (cache) {
                    Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = iterator.next();

                        if (entry.getValue().isExpired()) {
                            iterator.remove();
                        }
                    }
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        int total = cacheHits + cacheMisses;

        double hitRate = total == 0 ? 0 : (cacheHits * 100.0) / total;

        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + cacheHits);
        System.out.println("Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class DNSCacheApp {

    public static void main(String[] args) throws Exception {

        DNSCache dnsCache = new DNSCache(5);

        dnsCache.resolve("google.com");
        dnsCache.resolve("google.com");

        Thread.sleep(11000); // wait for TTL expiry

        dnsCache.resolve("google.com");

        dnsCache.resolve("facebook.com");
        dnsCache.resolve("twitter.com");
        dnsCache.resolve("github.com");
        dnsCache.resolve("openai.com");
        dnsCache.resolve("stackoverflow.com"); // triggers LRU eviction

        dnsCache.getCacheStats();
    }
}