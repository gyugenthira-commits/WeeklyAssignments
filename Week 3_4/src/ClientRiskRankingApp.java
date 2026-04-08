public class ClientRiskRankingApp {

    // ============== CLIENT CLASS ==============
    static class Client {
        String name;
        int riskScore;
        double accountBalance;

        public Client(String name, int riskScore, double accountBalance) {
            this.name = name;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        public String toString() {
            return name + ":" + riskScore + "($" + accountBalance + ")";
        }
    }

    // ============== BUBBLE SORT (ASCENDING) ==============
    public static void bubbleSortAscending(Client[] arr) {
        int n = arr.length;
        int swaps = 0;

        System.out.println("\nBubble Sort (Ascending by Risk Score):");

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].riskScore > arr[j + 1].riskScore) {

                    // Swap
                    Client temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    swaps++;
                    swapped = true;

                    // Visualize swap
                    System.out.println("Swapped: " + arr[j] + " <-> " + arr[j + 1]);
                }
            }

            if (!swapped) break;
        }

        printArray(arr);
        System.out.println("Total Swaps: " + swaps);
    }

    // ============== INSERTION SORT (DESC + BALANCE) ==============
    public static void insertionSortDescending(Client[] arr) {

        System.out.println("\nInsertion Sort (Descending by Risk + Balance):");

        for (int i = 1; i < arr.length; i++) {
            Client key = arr[i];
            int j = i - 1;

            // Sort by:
            // 1. Higher riskScore first
            // 2. If equal → higher balance first
            while (j >= 0 &&
                    (arr[j].riskScore < key.riskScore ||
                            (arr[j].riskScore == key.riskScore &&
                                    arr[j].accountBalance < key.accountBalance))) {

                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }

        printArray(arr);
    }

    // ============== TOP K HIGH RISK ==============
    public static void printTopK(Client[] arr, int k) {
        System.out.println("\nTop " + k + " High Risk Clients:");

        for (int i = 0; i < Math.min(k, arr.length); i++) {
            System.out.println(arr[i]);
        }
    }

    // ============== HELPER ======================
    public static void printArray(Client[] arr) {
        for (Client c : arr) {
            System.out.print(c + "  ");
        }
        System.out.println();
    }

    // ============== MAIN ========================
    public static void main(String[] args) {

        Client[] clients = {
                new Client("clientC", 80, 5000),
                new Client("clientA", 20, 2000),
                new Client("clientB", 50, 3000),
                new Client("clientD", 80, 7000), // same risk, higher balance
                new Client("clientE", 50, 1000)
        };

        System.out.println("Original Clients:");
        printArray(clients);

        // Bubble Sort (ascending)
        Client[] bubbleArray = clients.clone();
        bubbleSortAscending(bubbleArray);

        // Insertion Sort (descending)
        Client[] insertionArray = clients.clone();
        insertionSortDescending(insertionArray);

        // Top 10 highest risk
        printTopK(insertionArray, 10);
    }
}