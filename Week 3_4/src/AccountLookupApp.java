import java.util.*;

public class AccountLookupApp {

    // ============== LINEAR SEARCH =================
    public static int linearSearchFirst(String[] arr, String target) {
        int comparisons = 0;

        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i].equals(target)) {
                System.out.println("Linear First Occurrence Index: " + i +
                        " (Comparisons: " + comparisons + ")");
                return i;
            }
        }

        System.out.println("Not Found (Comparisons: " + comparisons + ")");
        return -1;
    }

    public static int linearSearchLast(String[] arr, String target) {
        int comparisons = 0;
        int result = -1;

        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i].equals(target)) {
                result = i;
            }
        }

        System.out.println("Linear Last Occurrence Index: " + result +
                " (Comparisons: " + comparisons + ")");
        return result;
    }

    // ============== BINARY SEARCH =================
    public static int binarySearch(String[] arr, String target) {
        int low = 0, high = arr.length - 1;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;

            int cmp = arr[mid].compareTo(target);

            if (cmp == 0) {
                System.out.println("Binary Found at Index: " + mid +
                        " (Comparisons: " + comparisons + ")");
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        System.out.println("Not Found (Comparisons: " + comparisons + ")");
        return -1;
    }

    // ============== COUNT OCCURRENCES =================
    public static int countOccurrences(String[] arr, String target) {
        int first = firstOccurrence(arr, target);
        int last = lastOccurrence(arr, target);

        if (first == -1) return 0;
        return last - first + 1;
    }

    // First occurrence using binary search
    private static int firstOccurrence(String[] arr, String target) {
        int low = 0, high = arr.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (arr[mid].equals(target)) {
                result = mid;
                high = mid - 1; // go left
            } else if (arr[mid].compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    // Last occurrence using binary search
    private static int lastOccurrence(String[] arr, String target) {
        int low = 0, high = arr.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (arr[mid].equals(target)) {
                result = mid;
                low = mid + 1; // go right
            } else if (arr[mid].compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    // ============== MAIN =================
    public static void main(String[] args) {

        String[] logs = {"accB", "accA", "accB", "accC"};

        System.out.println("Original Logs:");
        System.out.println(Arrays.toString(logs));

        // Linear Search
        linearSearchFirst(logs, "accB");
        linearSearchLast(logs, "accB");

        // Sort for Binary Search
        Arrays.sort(logs);

        System.out.println("\nSorted Logs:");
        System.out.println(Arrays.toString(logs));

        // Binary Search
        binarySearch(logs, "accB");

        // Count occurrences
        int count = countOccurrences(logs, "accB");
        System.out.println("Total Occurrences of accB: " + count);
    }
}