import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class InventoryManager {

    // HashMap for product stock
    private ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();

    // Waiting list (FIFO)
    private ConcurrentHashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new ConcurrentHashMap<>();


    // Add product to inventory
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);

        if (stock == null)
            return -1;

        return stock.get();
    }

    // Purchase item
    public String purchaseItem(String productId, int userId) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null)
            return "Product not found";

        synchronized (stock) {

            if (stock.get() > 0) {

                int remaining = stock.decrementAndGet();

                return "Success for user " + userId +
                        ", Remaining stock: " + remaining;
            }

            else {

                LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

                int position = queue.size() + 1;

                queue.put(userId, position);

                return "Stock finished. User " + userId +
                        " added to waiting list at position #" + position;
            }
        }
    }

    // Display waiting list
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        System.out.println("Waiting List:");

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() +
                    " -> Position " + entry.getValue());
        }
    }
}


public class FlashSaleSystem {

    public static void main(String[] args) throws InterruptedException {

        InventoryManager manager = new InventoryManager();

        // Product with 100 units
        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("Initial Stock: "
                + manager.checkStock("IPHONE15_256GB"));

        // Thread pool simulating concurrent buyers
        ExecutorService executor = Executors.newFixedThreadPool(50);

        for (int i = 1; i <= 120; i++) {

            final int userId = i;

            executor.submit(() -> {

                String result =
                        manager.purchaseItem("IPHONE15_256GB", userId);

                System.out.println(result);

            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("\nFinal Stock: "
                + manager.checkStock("IPHONE15_256GB"));

        manager.showWaitingList("IPHONE15_256GB");
    }
}