import java.util.ArrayList;
import java.util.List;

public class Inventory {
    // The list of items the player is currently carrying
    private ArrayList<Items> carriedItems = new ArrayList<>();

    // Add an item to the player's inventory
    public void addItem(Items item) {
        carriedItems.add(item);
    }

    // Remove an item from the inventory
    public void removeItem(Items item) {
        carriedItems.remove(item);
    }

    // Find an item by name in the inventory
    public Items getItemByName(String itemName) {
        for (Items item : carriedItems) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Check if inventory contains an item by name
    public boolean hasItem(String itemName) {
        return getItemByName(itemName) != null;
    }

    // Print a list of items currently carried
    public void listItems() {
        if (carriedItems.isEmpty()) {
            System.out.println("You're not carrying anything.");
        } else {
            System.out.println("Inventory:");
            for (Items item : carriedItems) {
                System.out.println("- " + item.getName());
            }
        }
    }

    // Return a snapshot (copy) of the current inventory
    public List<Items> snapshot() {
        return new ArrayList<>(carriedItems);
    }

    // Get the number of items in inventory
    public int size() {
        return carriedItems.size();
    }

    // Check if inventory is empty
    public boolean isEmpty() {
        return carriedItems.isEmpty();
    }
}
