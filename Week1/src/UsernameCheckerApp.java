import java.util.*;

class UsernameChecker {

    // Stores username -> userId
    private HashMap<String, Integer> userDatabase;

    // Tracks how many times a username was attempted
    private HashMap<String, Integer> attemptFrequency;

    public UsernameChecker() {
        userDatabase = new HashMap<>();
        attemptFrequency = new HashMap<>();

        // Sample existing users
        userDatabase.put("john_doe", 101);
        userDatabase.put("admin", 1);
        userDatabase.put("alex", 102);
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Update attempt count
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !userDatabase.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!userDatabase.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotSuggestion = username.replace("_", ".");
        if (!userDatabase.containsKey(dotSuggestion)) {
            suggestions.add(dotSuggestion);
        }

        return suggestions;
    }

    // Register a new username
    public void registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            userDatabase.put(username, userId);
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }
}

public class UsernameCheckerApp {

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        System.out.println("Check availability:");

        System.out.println("john_doe → " + checker.checkAvailability("john_doe"));
        System.out.println("jane_smith → " + checker.checkAvailability("jane_smith"));

        System.out.println("\nSuggestions for john_doe:");
        System.out.println(checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println("\nMost attempted username:");
        System.out.println(checker.getMostAttempted());
    }
}