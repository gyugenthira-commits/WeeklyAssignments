import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsSystem {

    // pageUrl -> total visits
    private HashMap<String, Integer> pageVisits = new HashMap<>();

    // pageUrl -> set of unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> visit count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Process incoming page view event
    public void processEvent(PageViewEvent event) {

        // Update visit count
        pageVisits.put(event.url,
                pageVisits.getOrDefault(event.url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageVisits.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            topPages.add(pq.poll());
            count++;
        }

        return topPages;
    }

    // Print dashboard
    public void getDashboard() {

        System.out.println("\n====== REAL-TIME ANALYTICS DASHBOARD ======");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;
        for (int count : trafficSources.values()) {
            total += count;
        }

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = (count * 100.0) / total;

            System.out.println(source + ": "
                    + String.format("%.1f", percent) + "%");
        }
    }
}

public class RealTimeAnalyticsApp {

    public static void main(String[] args) throws Exception {

        AnalyticsSystem analytics = new AnalyticsSystem();

        // Simulated streaming events
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_789", "direct"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_321", "google"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_654", "google"));
        analytics.processEvent(new PageViewEvent("/tech/ai-news", "user_777", "facebook"));
        analytics.processEvent(new PageViewEvent("/tech/ai-news", "user_888", "google"));

        // Update dashboard every 5 seconds
        for (int i = 0; i < 3; i++) {
            analytics.getDashboard();
            Thread.sleep(5000);
        }
    }
}