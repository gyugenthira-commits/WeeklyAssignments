import java.util.*;

public class AutoCompleteSystemApp {

    // =============== TRIE NODE =================
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
        boolean isEndOfWord = false;
    }

    // =============== AUTOCOMPLETE SYSTEM =================
    static class AutocompleteSystem {

        private TrieNode root;
        private Map<String, Integer> globalFrequency;
        private final int TOP_K = 10;

        public AutocompleteSystem() {
            root = new TrieNode();
            globalFrequency = new HashMap<>();
        }

        // Insert query
        public void insert(String query, int freq) {
            globalFrequency.put(query, globalFrequency.getOrDefault(query, 0) + freq);

            TrieNode node = root;
            for (char c : query.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);

                node.frequencyMap.put(query, globalFrequency.get(query));
            }
            node.isEndOfWord = true;
        }

        // Update frequency
        public void updateFrequency(String query) {
            insert(query, 1);
        }

        // Search prefix
        public List<String> search(String prefix) {
            TrieNode node = root;

            for (char c : prefix.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return handleTypo(prefix);
                }
                node = node.children.get(c);
            }

            return getTopK(node.frequencyMap);
        }

        // ===== FIXED METHOD =====
        private List<String> getTopK(Map<String, Integer> map) {
            PriorityQueue<Map.Entry<String, Integer>> minHeap =
                    new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                minHeap.offer(entry);
                if (minHeap.size() > TOP_K) {
                    minHeap.poll();
                }
            }

            List<String> result = new ArrayList<>();

            while (!minHeap.isEmpty()) {
                Map.Entry<String, Integer> entry = minHeap.poll();
                result.add(entry.getKey() + " (" + entry.getValue() + ")");
            }

            Collections.reverse(result);
            return result;
        }

        // Typo handling (edit distance = 1)
        private List<String> handleTypo(String prefix) {
            List<String> suggestions = new ArrayList<>();

            for (String query : globalFrequency.keySet()) {
                String sub = query.substring(0, Math.min(prefix.length(), query.length()));
                if (isEditDistanceOne(prefix, sub)) {
                    suggestions.add(query);
                }
            }

            suggestions.sort((a, b) -> globalFrequency.get(b) - globalFrequency.get(a));

            List<String> result = new ArrayList<>();
            for (int i = 0; i < Math.min(TOP_K, suggestions.size()); i++) {
                String q = suggestions.get(i);
                result.add(q + " (" + globalFrequency.get(q) + ")");
            }

            return result;
        }

        // Edit distance = 1 check
        private boolean isEditDistanceOne(String s1, String s2) {
            if (Math.abs(s1.length() - s2.length()) > 1) return false;

            int i = 0, j = 0, count = 0;

            while (i < s1.length() && j < s2.length()) {
                if (s1.charAt(i) != s2.charAt(j)) {
                    if (count == 1) return false;
                    count++;

                    if (s1.length() > s2.length()) i++;
                    else if (s1.length() < s2.length()) j++;
                    else {
                        i++;
                        j++;
                    }
                } else {
                    i++;
                    j++;
                }
            }
            return true;
        }
    }

    // =============== MAIN =================
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // Sample data
        system.insert("java tutorial", 1234567);
        system.insert("javascript", 987654);
        system.insert("java download", 456789);
        system.insert("java 21 features", 100);
        system.insert("java spring boot", 500000);
        system.insert("java interview questions", 300000);

        // Search
        System.out.println("Search results for 'jav':");
        List<String> results = system.search("jav");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }

        // Update frequency
        System.out.println("\nUpdating frequency for 'java 21 features'...");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        // Search again
        System.out.println("\nSearch results after update:");
        results = system.search("jav");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }
    }
}