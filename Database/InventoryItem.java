import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an item in the inventory.
 * Uses JavaFX properties to allow for data binding in TableViews.
 */
public class InventoryItem {
    // The name of the inventory item
    private final SimpleStringProperty itemName;
    
    // The quantity available in stock
    private final SimpleIntegerProperty quantity;
    
    // The unit of measurement (e.g., "kg", "cups")
    private final SimpleStringProperty unit;

    /**
     * Constructor to initialize an InventoryItem.
     * @param itemName The name of the item
     * @param quantity The current stock quantity
     * @param unit The unit of measurement
     */
    public InventoryItem(String itemName, int quantity, String unit) {
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unit = new SimpleStringProperty(unit);
    }

    /**
     * Gets the item name.
     * @return The name as a String
     */
    public String getItemName() { return itemName.get(); }
    
    /**
     * Gets the quantity.
     * @return The quantity as an int
     */
    public int getQuantity() { return quantity.get(); }
    
    /**
     * Gets the unit of measurement.
     * @return The unit as a String
     */
    public String getUnit() { return unit.get(); }
}