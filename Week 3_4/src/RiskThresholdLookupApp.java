import java.util.*;

public class RiskThresholdLookupApp {

    // ============== LINEAR SEARCH =================
    public static void linearSearch(int[] arr, int target) {
        int comparisons = 0;
        boolean found = false;

        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i] == target) {
                System.out.println("Linear: Found at index " + i +
                        " (" + comparisons + " comparisons)");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Linear: Not found (" + comparisons + " comparisons)");
        }
    }

    // ============== BINARY SEARCH INSERT POSITION =================
    public static int lowerBound(int[] arr, int target) {
        int low = 0, high = arr.length;
        int comparisons = 0;

        while (low < high) {
            int mid = (low + high) / 2;
            comparisons++;

            if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        System.out.println("Insertion Index (lower_bound): " + low +
                " (" + comparisons + " comparisons)");
        return low;
    }

    // ============== FLOOR =================
    public static Integer findFloor(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        Integer floor = null;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;

            if (arr[mid] == target) {
                floor = arr[mid];
                break;
            } else if (arr[mid] < target) {
                floor = arr[mid];
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        System.out.println("Floor (" + target + "): " + floor +
                " (" + comparisons + " comparisons)");
        return floor;
    }

    // ============== CEILING =================
    public static Integer findCeiling(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        Integer ceil = null;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;

            if (arr[mid] == target) {
                ceil = arr[mid];
                break;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                ceil = arr[mid];
                high = mid - 1;
            }
        }

        System.out.println("Ceiling (" + target + "): " + ceil +
                " (" + comparisons + " comparisons)");
        return ceil;
    }

    // ============== MAIN =================
    public static void main(String[] args) {

        int[] unsorted = {50, 10, 100, 25};
        int target = 30;

        System.out.println("Unsorted Risks:");
        System.out.println(Arrays.toString(unsorted));

        // Linear Search
        linearSearch(unsorted, target);

        // Sort for binary operations
        Arrays.sort(unsorted);

        System.out.println("\nSorted Risks:");
        System.out.println(Arrays.toString(unsorted));

        // Binary insertion point
        lowerBound(unsorted, target);

        // Floor & Ceiling
        findFloor(unsorted, target);
        findCeiling(unsorted, target);
    }
}