import java.util.*;

public class TransactionSortingApp {

    // ============== TRANSACTION CLASS ==============
    static class Transaction {
        String id;
        double fee;
        String timestamp; // HH:MM

        public Transaction(String id, double fee, String timestamp) {
            this.id = id;
            this.fee = fee;
            this.timestamp = timestamp;
        }

        // Convert HH:MM → minutes (for comparison)
        public int getTimeInMinutes() {
            String[] parts = timestamp.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        }

        public String toString() {
            return id + ":" + fee + "@" + timestamp;
        }
    }

    // ============== BUBBLE SORT (BY FEE) ==============
    public static void bubbleSortByFee(List<Transaction> list) {
        int n = list.size();
        int passes = 0, swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            passes++;

            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    Collections.swap(list, j, j + 1);
                    swaps++;
                    swapped = true;
                }
            }

            // Early termination
            if (!swapped) break;
        }

        System.out.println("\nBubble Sort Result:");
        printList(list);
        System.out.println("Passes: " + passes + ", Swaps: " + swaps);
    }

    // ============== INSERTION SORT (FEE + TIMESTAMP) ==============
    public static void insertionSortByFeeAndTime(List<Transaction> list) {
        int n = list.size();

        for (int i = 1; i < n; i++) {
            Transaction key = list.get(i);
            int j = i - 1;

            // Stable: shift only when strictly greater
            while (j >= 0 &&
                    (list.get(j).fee > key.fee ||
                            (list.get(j).fee == key.fee &&
                                    list.get(j).getTimeInMinutes() > key.getTimeInMinutes()))) {

                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }

        System.out.println("\nInsertion Sort Result (Fee + Timestamp):");
        printList(list);
    }

    // ============== OUTLIER DETECTION ==============
    public static void findHighFeeOutliers(List<Transaction> list) {
        System.out.println("\nHigh-fee Outliers (> 50):");

        boolean found = false;
        for (Transaction t : list) {
            if (t.fee > 50) {
                System.out.println(t);
                found = true;
            }
        }

        if (!found) {
            System.out.println("None");
        }
    }

    // ============== HELPER PRINT ==============
    public static void printList(List<Transaction> list) {
        for (Transaction t : list) {
            System.out.print(t + "  ");
        }
        System.out.println();
    }

    // ============== MAIN ======================
    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction("id1", 10.5, "10:00"));
        transactions.add(new Transaction("id2", 25.0, "09:30"));
        transactions.add(new Transaction("id3", 5.0, "10:15"));
        transactions.add(new Transaction("id4", 60.0, "11:00")); // outlier
        transactions.add(new Transaction("id5", 25.0, "09:00")); // duplicate fee

        System.out.println("Original Transactions:");
        printList(transactions);

        // Choose algorithm based on size
        if (transactions.size() <= 100) {
            bubbleSortByFee(new ArrayList<>(transactions));
        } else {
            insertionSortByFeeAndTime(new ArrayList<>(transactions));
        }

        // Always show insertion sort (for requirement)
        insertionSortByFeeAndTime(new ArrayList<>(transactions));

        // Outlier detection
        findHighFeeOutliers(transactions);
    }
}