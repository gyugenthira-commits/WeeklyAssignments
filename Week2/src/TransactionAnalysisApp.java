import java.util.*;

public class TransactionAnalysisApp {

    // ============== TRANSACTION CLASS ==============
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long timestamp; // in milliseconds

        public Transaction(int id, int amount, String merchant, String account, long timestamp) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.timestamp = timestamp;
        }

        public String toString() {
            return "{id:" + id + ", amt:" + amount + ", merchant:" + merchant + "}";
        }
    }

    // ============== SERVICE ========================
    static class TransactionService {

        private List<Transaction> transactions;

        public TransactionService(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        // ======== 1. CLASSIC TWO-SUM ========
        public List<List<Transaction>> findTwoSum(int target) {
            Map<Integer, Transaction> map = new HashMap<>();
            List<List<Transaction>> result = new ArrayList<>();

            for (Transaction t : transactions) {
                int complement = target - t.amount;

                if (map.containsKey(complement)) {
                    result.add(Arrays.asList(map.get(complement), t));
                }
                map.put(t.amount, t);
            }
            return result;
        }

        // ======== 2. TWO-SUM WITH TIME WINDOW (1 hour) ========
        public List<List<Transaction>> findTwoSumWithTimeWindow(int target, long windowMillis) {
            List<List<Transaction>> result = new ArrayList<>();
            Map<Integer, List<Transaction>> map = new HashMap<>();

            for (Transaction t : transactions) {

                int complement = target - t.amount;

                if (map.containsKey(complement)) {
                    for (Transaction prev : map.get(complement)) {
                        if (Math.abs(t.timestamp - prev.timestamp) <= windowMillis) {
                            result.add(Arrays.asList(prev, t));
                        }
                    }
                }

                map.putIfAbsent(t.amount, new ArrayList<>());
                map.get(t.amount).add(t);
            }

            return result;
        }

        // ======== 3. K-SUM ========
        public List<List<Transaction>> findKSum(int k, int target) {
            List<List<Transaction>> result = new ArrayList<>();
            backtrack(0, k, target, new ArrayList<>(), result);
            return result;
        }

        private void backtrack(int start, int k, int target,
                               List<Transaction> current,
                               List<List<Transaction>> result) {

            if (k == 0 && target == 0) {
                result.add(new ArrayList<>(current));
                return;
            }

            if (k == 0 || start >= transactions.size()) return;

            for (int i = start; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);

                current.add(t);
                backtrack(i + 1, k - 1, target - t.amount, current, result);
                current.remove(current.size() - 1);
            }
        }

        // ======== 4. DUPLICATE DETECTION ========
        public List<String> detectDuplicates() {
            Map<String, Map<Integer, Set<String>>> map = new HashMap<>();
            List<String> result = new ArrayList<>();

            for (Transaction t : transactions) {
                map.putIfAbsent(t.merchant, new HashMap<>());
                Map<Integer, Set<String>> amountMap = map.get(t.merchant);

                amountMap.putIfAbsent(t.amount, new HashSet<>());
                Set<String> accounts = amountMap.get(t.amount);

                accounts.add(t.account);

                if (accounts.size() > 1) {
                    result.add("Duplicate → Merchant: " + t.merchant +
                            ", Amount: " + t.amount +
                            ", Accounts: " + accounts);
                }
            }

            return result;
        }
    }

    // ============== MAIN ========================
    public static void main(String[] args) {

        long now = System.currentTimeMillis();

        List<Transaction> txns = Arrays.asList(
                new Transaction(1, 500, "StoreA", "acc1", now),
                new Transaction(2, 300, "StoreB", "acc2", now + 1000),
                new Transaction(3, 200, "StoreC", "acc3", now + 2000),
                new Transaction(4, 500, "StoreA", "acc2", now + 3000), // duplicate
                new Transaction(5, 700, "StoreD", "acc4", now + 4000)
        );

        TransactionService service = new TransactionService(txns);

        // 1. Two Sum
        System.out.println("Two-Sum (target=500):");
        for (List<Transaction> pair : service.findTwoSum(500)) {
            System.out.println(pair);
        }

        // 2. Two Sum with Time Window (1 hour)
        System.out.println("\nTwo-Sum with Time Window:");
        long oneHour = 60 * 60 * 1000;
        for (List<Transaction> pair : service.findTwoSumWithTimeWindow(500, oneHour)) {
            System.out.println(pair);
        }

        // 3. K-Sum
        System.out.println("\nK-Sum (k=3, target=1000):");
        for (List<Transaction> group : service.findKSum(3, 1000)) {
            System.out.println(group);
        }

        // 4. Duplicate Detection
        System.out.println("\nDuplicate Transactions:");
        for (String dup : service.detectDuplicates()) {
            System.out.println(dup);
        }
    }
}