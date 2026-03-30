import java.util.*;

public class ParkingLotApp {

    // ============== SLOT STATUS ENUM ==============
    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    // ============== PARKING SLOT ==================
    static class ParkingSlot {
        String licensePlate;
        long entryTime;
        Status status;

        public ParkingSlot() {
            this.status = Status.EMPTY;
        }
    }

    // ============== PARKING LOT ===================
    static class ParkingLot {

        private ParkingSlot[] table;
        private int capacity;
        private int size;
        private int totalProbes;
        private Map<Integer, Integer> hourlyTraffic; // hour → count

        public ParkingLot(int capacity) {
            this.capacity = capacity;
            this.table = new ParkingSlot[capacity];
            this.size = 0;
            this.totalProbes = 0;
            this.hourlyTraffic = new HashMap<>();

            for (int i = 0; i < capacity; i++) {
                table[i] = new ParkingSlot();
            }
        }

        // ============== HASH FUNCTION ==============
        private int hash(String licensePlate) {
            int hash = 0;
            for (char c : licensePlate.toCharArray()) {
                hash = (hash * 31 + c) % capacity;
            }
            return Math.abs(hash);
        }

        // ============== PARK VEHICLE ==============
        public String parkVehicle(String licensePlate) {
            if (size == capacity) {
                return "Parking Full!";
            }

            int index = hash(licensePlate);
            int probes = 0;

            while (table[index].status == Status.OCCUPIED) {
                index = (index + 1) % capacity;
                probes++;
            }

            table[index].licensePlate = licensePlate;
            table[index].entryTime = System.currentTimeMillis();
            table[index].status = Status.OCCUPIED;

            size++;
            totalProbes += probes;

            // Track peak hour
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            hourlyTraffic.put(hour, hourlyTraffic.getOrDefault(hour, 0) + 1);

            return "Assigned spot #" + index + " (" + probes + " probes)";
        }

        // ============== EXIT VEHICLE ==============
        public String exitVehicle(String licensePlate) {
            int index = hash(licensePlate);
            int start = index;

            while (table[index].status != Status.EMPTY) {
                if (table[index].status == Status.OCCUPIED &&
                        table[index].licensePlate.equals(licensePlate)) {

                    long exitTime = System.currentTimeMillis();
                    long durationMillis = exitTime - table[index].entryTime;

                    double hours = durationMillis / (1000.0 * 60 * 60);
                    double fee = calculateFee(hours);

                    table[index].status = Status.DELETED;
                    table[index].licensePlate = null;

                    size--;

                    return "Spot #" + index + " freed, Duration: " +
                            String.format("%.2f", hours) + "h, Fee: $" +
                            String.format("%.2f", fee);
                }

                index = (index + 1) % capacity;
                if (index == start) break;
            }

            return "Vehicle not found!";
        }

        // ============== FIND NEAREST AVAILABLE ==============
        public int findNearestAvailable() {
            for (int i = 0; i < capacity; i++) {
                if (table[i].status != Status.OCCUPIED) {
                    return i;
                }
            }
            return -1;
        }

        // ============== BILLING ==============
        private double calculateFee(double hours) {
            double ratePerHour = 5.0; // simple pricing
            return Math.ceil(hours) * ratePerHour;
        }

        // ============== STATISTICS ==============
        public String getStatistics() {
            double occupancy = (size * 100.0) / capacity;
            double avgProbes = size == 0 ? 0 : (double) totalProbes / size;

            int peakHour = -1;
            int maxCount = 0;

            for (Map.Entry<Integer, Integer> entry : hourlyTraffic.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    peakHour = entry.getKey();
                }
            }

            return "Occupancy: " + String.format("%.2f", occupancy) + "%" +
                    ", Avg Probes: " + String.format("%.2f", avgProbes) +
                    ", Peak Hour: " + (peakHour == -1 ? "N/A" : peakHour + "-" + (peakHour + 1));
        }
    }

    // ============== MAIN ===================
    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot(500);

        // Park vehicles
        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        // Wait to simulate time
        Thread.sleep(2000);

        // Exit vehicle
        System.out.println(lot.exitVehicle("ABC-1234"));

        // Nearest available
        System.out.println("Nearest available spot: #" + lot.findNearestAvailable());

        // Stats
        System.out.println(lot.getStatistics());
    }
}