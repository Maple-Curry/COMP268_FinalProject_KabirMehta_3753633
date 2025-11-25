import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Control {
    private Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // Direction aliases
        ALIASES.put("n", "go north");
        ALIASES.put("s", "go south");
        ALIASES.put("e", "go east");
        ALIASES.put("w", "go west");
        ALIASES.put("north", "go north");
        ALIASES.put("south", "go south");
        ALIASES.put("east", "go east");
        ALIASES.put("west", "go west");
        // Command aliases
        ALIASES.put("i", "inventory");
        ALIASES.put("inv", "inventory");
        ALIASES.put("l", "look");
        ALIASES.put("x", "look");
        ALIASES.put("examine", "look");
        ALIASES.put("exit", "quit");
        ALIASES.put("q", "quit");
        ALIASES.put("?", "help");
        ALIASES.put("get", "take");
        ALIASES.put("pickup", "take");
        ALIASES.put("pick", "take");
        ALIASES.put("grab", "take");
    }

    /**
     * Read and normalize user input.
     * Handles aliases and trims/lowercases input.
     */
    public String getCommand() {
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();
        return normalizeCommand(input);
    }

    /**
     * Normalize command by applying aliases.
     */
    private String normalizeCommand(String input) {
        // Check for exact alias match first
        if (ALIASES.containsKey(input)) {
            return ALIASES.get(input);
        }
        
        // Check if first word is an alias that needs to preserve arguments
        String[] parts = input.split(" ", 2);
        if (parts.length > 1 && ALIASES.containsKey(parts[0])) {
            String aliased = ALIASES.get(parts[0]);
            // Handle aliases like "get" -> "take" that need to preserve the rest
            if (aliased.contains(" ")) {
                // Alias like "n" -> "go north" doesn't need args
                return aliased;
            } else {
                return aliased + " " + parts[1];
            }
        }
        
        return input;
    }

    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  go [direction]  - Move in a direction (north/south/east/west or n/s/e/w)");
        System.out.println("  look            - Look around the current location");
        System.out.println("  take [item]     - Pick up an item");
        System.out.println("  drop [item]     - Drop an item from your inventory");
        System.out.println("  use [item]      - Use an item");
        System.out.println("  talk [name]     - Talk to a character");
        System.out.println("  search          - Search for hidden items");
        System.out.println("  inventory (i)   - Show your inventory");
        System.out.println("  stash [item]    - Store item in safe room stash");
        System.out.println("  retrieve [item] - Retrieve item from safe room stash");
        System.out.println("  hint            - Get a hint about what to do");
        System.out.println("  help (?)        - Show this help message");
        System.out.println("  quit            - Quit the game");
    }

    public void printHint(Locations currentLocation, Inventory inventory) {
        String suggestions = currentLocation.getSuggestions();
        if (!suggestions.isEmpty()) {
            System.out.println("Hint: " + suggestions);
        } else if (inventory.isEmpty()) {
            System.out.println("Hint: Try exploring and picking up items you find.");
        } else {
            System.out.println("Hint: Try using items you've collected, or explore other areas.");
        }
    }
}
