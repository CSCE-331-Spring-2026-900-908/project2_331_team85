/**
 * Container class for data models used throughout the application.
 * These inner classes represent entities fetched from the database
 * or aggregated for reports.
 */
public class Models {

    /**
     * Represents a menu item available for sale.
     * Used for displaying menu options in the UI.
     */
    public static class MenuItem {
        private int id;
        private String name;
        private double price;

        public MenuItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    /**
     * Represents an item in the inventory.
     * Tracks stock levels and units.
     */
    public static class InventoryItem {
        private int id;
        private String name;
        private int quantity;
        private String unit;

        public InventoryItem(int id, String name, int quantity, String unit) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public String getUnit() { return unit; }
    }

    /**
     * Represents a sales record for reporting.
     * Aggregates quantity sold and revenue generated per item.
     */
    public static class SalesItem {
        private String itemName;
        private int quantity;
        private double revenue;

        public SalesItem(String itemName, int quantity, double revenue) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.revenue = revenue;
        }

        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public double getRevenue() { return revenue; }
    }

    /**
     * Represents inventory usage data.
     * Used for tracking how much of an ingredient has been consumed.
     */
    public static class UsageItem {
        private String itemName;
        private double quantityUsed;

        public UsageItem(String itemName, double quantityUsed) {
            this.itemName = itemName;
            this.quantityUsed = quantityUsed;
        }

        public String getItemName() { return itemName; }
        public double getQuantityUsed() { return quantityUsed; }
    }
}