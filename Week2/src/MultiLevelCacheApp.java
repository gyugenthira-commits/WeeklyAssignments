import java.util.*;

public class MultiLevelCacheApp {

    // ============== VIDEO DATA =================
    static class Video {
        String id;
        String content;

        public Video(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String toString() {
            return id;
        }
    }

    // ============== LRU CACHE =================
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // access-order
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    // ============== CACHE SYSTEM =================
    static class CacheSystem {

        private LRUCache<String, Video> L1;
        private LRUCache<String, Video> L2;
        private Map<String, Video> L3; // DB simulation

        private Map<String, Integer> accessCount;

        // Stats
        private int l1Hits = 0, l2Hits = 0, l3Hits = 0;
        private int totalRequests = 0;

        private final int PROMOTION_THRESHOLD = 2;

        public CacheSystem() {
            L1 = new LRUCache<>(10000);
            L2 = new LRUCache<>(100000);
            L3 = new HashMap<>();
            accessCount = new HashMap<>();
        }

        // ============== GET VIDEO =================
        public Video getVideo(String videoId) {
            totalRequests++;

            // L1 check
            if (L1.containsKey(videoId)) {
                l1Hits++;
                System.out.println("L1 HIT (0.5ms)");
                return L1.get(videoId);
            }

            // L2 check
            if (L2.containsKey(videoId)) {
                l2Hits++;
                System.out.println("L2 HIT (5ms)");

                Video video = L2.get(videoId);
                incrementAccess(videoId);

                // Promote to L1
                if (accessCount.get(videoId) >= PROMOTION_THRESHOLD) {
                    L1.put(videoId, video);
                    System.out.println("Promoted to L1");
                }

                return video;
            }

            // L3 (DB)
            if (L3.containsKey(videoId)) {
                l3Hits++;
                System.out.println("L3 HIT (150ms)");

                Video video = L3.get(videoId);

                // Add to L2
                L2.put(videoId, video);
                accessCount.put(videoId, 1);

                return video;
            }

            System.out.println("Video not found!");
            return null;
        }

        // ============== ADD VIDEO TO DB =================
        public void addVideoToDB(Video video) {
            L3.put(video.id, video);
        }

        // ============== ACCESS COUNT =================
        private void incrementAccess(String videoId) {
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
        }

        // ============== INVALIDATION =================
        public void invalidate(String videoId) {
            L1.remove(videoId);
            L2.remove(videoId);
            L3.remove(videoId);
            accessCount.remove(videoId);

            System.out.println("Cache invalidated for " + videoId);
        }

        // ============== STATISTICS =================
        public void getStatistics() {
            double l1Rate = (totalRequests == 0) ? 0 : (l1Hits * 100.0 / totalRequests);
            double l2Rate = (totalRequests == 0) ? 0 : (l2Hits * 100.0 / totalRequests);
            double l3Rate = (totalRequests == 0) ? 0 : (l3Hits * 100.0 / totalRequests);

            double avgTime =
                    (l1Hits * 0.5 + l2Hits * 5 + l3Hits * 150) / (double) totalRequests;

            System.out.println("\n===== CACHE STATISTICS =====");
            System.out.println("L1 Hit Rate: " + String.format("%.2f", l1Rate) + "%");
            System.out.println("L2 Hit Rate: " + String.format("%.2f", l2Rate) + "%");
            System.out.println("L3 Hit Rate: " + String.format("%.2f", l3Rate) + "%");
            System.out.println("Overall Avg Time: " + String.format("%.2f", avgTime) + " ms");
        }
    }

    // ============== MAIN =================
    public static void main(String[] args) {

        CacheSystem cache = new CacheSystem();

        // Populate DB (L3)
        cache.addVideoToDB(new Video("video_123", "Movie A"));
        cache.addVideoToDB(new Video("video_999", "Movie B"));

        // First access
        System.out.println("Request 1:");
        cache.getVideo("video_123");

        // Second access → should promote
        System.out.println("\nRequest 2:");
        cache.getVideo("video_123");

        // Third access → L1 hit
        System.out.println("\nRequest 3:");
        cache.getVideo("video_123");

        // Another video
        System.out.println("\nRequest 4:");
        cache.getVideo("video_999");

        // Invalidate
        System.out.println("\nInvalidating video_123:");
        cache.invalidate("video_123");

        // Stats
        cache.getStatistics();
    }
}