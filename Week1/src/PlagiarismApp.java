import java.util.*;

class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex;

    // documentId -> document text
    private HashMap<String, String> documents;

    private int N = 5; // 5-gram

    public PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documents = new HashMap<>();
    }

    // Add a document to the database
    public void addDocument(String docId, String text) {

        documents.put(docId, text);

        List<String> ngrams = generateNGrams(text);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());

            ngramIndex.get(gram).add(docId);
        }
    }

    // Generate n-grams from text
    private List<String> generateNGrams(String text) {

        List<String> grams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }

    // Analyze a new document
    public void analyzeDocument(String docId, String text) {

        List<String> grams = generateNGrams(text);

        System.out.println("Extracted " + grams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {

            if (ngramIndex.containsKey(gram)) {

                for (String matchedDoc : ngramIndex.get(gram)) {

                    matchCount.put(matchedDoc,
                            matchCount.getOrDefault(matchedDoc, 0) + 1);
                }
            }
        }

        // Calculate similarity
        for (String matchedDoc : matchCount.keySet()) {

            int matches = matchCount.get(matchedDoc);

            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + matchedDoc + "\"");

            System.out.println("Similarity: " +
                    String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }
}

public class PlagiarismApp {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // Existing essays
        detector.addDocument("essay_089",
                "machine learning is a field of artificial intelligence that uses data");

        detector.addDocument("essay_092",
                "machine learning is a field of artificial intelligence that uses data and algorithms to learn patterns");

        // New student submission
        String newEssay = "machine learning is a field of artificial intelligence that uses data and algorithms";

        System.out.println("Analyzing new document...\n");

        detector.analyzeDocument("essay_123", newEssay);
    }
}