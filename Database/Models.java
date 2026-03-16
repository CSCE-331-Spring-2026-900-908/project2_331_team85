/**
 * Container class for data models used throughout the application.
 * These inner classes represent entities fetched from the database
 * or aggregated for reports.
 * 
 * @author Team 85
 * @version 1.0
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

        /**
         * Constructs a new MenuItem.
         * 
         * @param id The database ID of the menu item
         * @param name The name of the menu item
         * @param price The price of the menu item
         */
        public MenuItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        /**
         * @return The ID of the menu item
         */
        public int getId() { return id; }
        /**
         * @return The name of the menu item
         */
        public String getName() { return name; }
        /**
         * @return The price of the menu item
         */
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

        /**
         * Constructs a new InventoryItem.
         * 
         * @param id The database ID of the inventory item
         * @param name The name of the inventory item
         * @param quantity The current stock quantity
         * @param unit The unit of measurement
         */
        public InventoryItem(int id, String name, int quantity, String unit) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        /**
         * @return The ID of the inventory item
         */
        public int getId() { return id; }
        /**
         * @return The name of the inventory item
         */
        public String getName() { return name; }
        /**
         * @return The quantity in stock
         */
        public int getQuantity() { return quantity; }
        /**
         * @return The unit of measurement
         */
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

        /**
         * Constructs a new SalesItem.
         * 
         * @param itemName The name of the item sold
         * @param quantity The quantity sold
         * @param revenue The total revenue generated
         */
        public SalesItem(String itemName, int quantity, double revenue) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.revenue = revenue;
        }

        /**
         * @return The name of the item
         */
        public String getItemName() { return itemName; }
        /**
         * @return The quantity sold
         */
        public int getQuantity() { return quantity; }
        /**
         * @return The revenue generated
         */
        public double getRevenue() { return revenue; }
    }

    /**
     * Represents inventory usage data.
     * Used for tracking how much of an ingredient has been consumed.
     */
    public static class UsageItem {
        private String itemName;
        private double quantityUsed;

        /**
         * Constructs a new UsageItem.
         * 
         * @param itemName The name of the item used
         * @param quantityUsed The amount of the item consumed
         */
        public UsageItem(String itemName, double quantityUsed) {
            this.itemName = itemName;
            this.quantityUsed = quantityUsed;
        }

        /**
         * @return The name of the item
         */
        public String getItemName() { return itemName; }
        /**
         * @return The quantity used
         */
        public double getQuantityUsed() { return quantityUsed; }
    }
}