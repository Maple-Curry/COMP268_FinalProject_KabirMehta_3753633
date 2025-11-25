import java.util.Random;

/**
 * Static event hooks for game events.
 * Handles hidden item reveals and inventory-triggered encounters.
 */
public class GameEventSystem {
    private static final Random random = new Random();

    /**
     * Called when player enters a new location.
     * May trigger random encounters based on inventory state.
     */
    public static void onLocationEnter(Locations location, Inventory inventory) {
        // Random encounter chance increases with inventory size
        if (!inventory.isEmpty() && random.nextInt(10) < inventory.size()) {
            triggerRandomEncounter(location, inventory);
        }
    }

    /**
     * Called when player picks up an item.
     * May reveal hidden items or trigger events.
     */
    public static void onItemPickup(Items item, Locations location, Inventory inventory) {
        // Example: picking up the watch reveals something
        if (item.getName().equalsIgnoreCase("watch")) {
            System.out.println("As you pick up the watch, time seems to shimmer around you...");
        }
        
        // Check for hidden items that might be revealed
        for (Items hiddenItem : location.getItems()) {
            if (hiddenItem.isHidden()) {
                // Chance to reveal hidden items when picking up other items
                if (random.nextInt(5) == 0) {
                    hiddenItem.reveal();
                    System.out.println("You notice something you missed before: " + hiddenItem.getName());
                }
            }
        }
    }

    /**
     * Called when player uses an item.
     * Handles special item interactions.
     */
    public static void onItemUse(Items item, Locations location, Inventory inventory) {
        String itemName = item.getName().toLowerCase();
        
        switch (itemName) {
            case "bottle":
                System.out.println("You drink from the bottle. Everything seems to grow larger around you!");
                break;
            case "cake":
                System.out.println("You take a bite of the cake. You feel yourself growing!");
                break;
            case "mushroom":
                System.out.println("You nibble the mushroom. Strange sensations wash over you...");
                break;
            case "fan":
                System.out.println("You wave the fan. A cool breeze refreshes you.");
                break;
            default:
                System.out.println("You use the " + item.getName() + ", but nothing special happens.");
        }
    }

    /**
     * Called when player searches the current location.
     * Reveals hidden items.
     */
    public static void onSearch(Locations location) {
        boolean foundSomething = false;
        for (Items item : location.getItems()) {
            if (item.isHidden()) {
                item.reveal();
                System.out.println("You found: " + item.getName() + " - " + item.getDescription());
                foundSomething = true;
            }
        }
        if (!foundSomething) {
            System.out.println("You search carefully but find nothing hidden.");
        }
    }

    /**
     * Trigger a random encounter based on current state.
     */
    private static void triggerRandomEncounter(Locations location, Inventory inventory) {
        String[] encounters = {
            "A playing card soldier marches past, eyeing you suspiciously.",
            "The Cheshire Cat's grin appears briefly, then fades away.",
            "You hear the distant sound of 'Off with their heads!'",
            "A white rose floats by, dripping red paint.",
            "Time seems to skip for a moment..."
        };
        
        int index = random.nextInt(encounters.length);
        System.out.println("\n" + encounters[index] + "\n");
    }

    /**
     * Check if player has required items for a puzzle/gate.
     */
    public static boolean hasRequiredItems(Inventory inventory, String... itemNames) {
        for (String name : itemNames) {
            if (!inventory.hasItem(name)) {
                return false;
            }
        }
        return true;
    }
}
