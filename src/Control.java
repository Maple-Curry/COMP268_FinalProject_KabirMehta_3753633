import java.util.Scanner;

/**
 * Control.java - Handles user input and command processing.
 * Provides input sanitization, alias mapping, and help display.
 */
public class Control {
    private Scanner scanner = new Scanner(System.in);

    /**
     * Get a command from the user, with input sanitization.
     * Removes punctuation, trims whitespace, and converts to lowercase.
     * @return The sanitized command string
     */
    public String getCommand() {
        System.out.print("> ");
        String input = scanner.nextLine();
        return sanitizeInput(input);
    }

    /**
     * Sanitize user input by removing punctuation and normalizing whitespace.
     * @param input The raw user input
     * @return The sanitized input string
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove punctuation except spaces, convert to lowercase, trim
        return input.replaceAll("[^a-zA-Z0-9\\s]", "")
                    .toLowerCase()
                    .trim()
                    .replaceAll("\\s+", " ");
    }

    /**
     * Expand direction shortcuts to full "go direction" commands.
     * @param command The original command
     * @return The expanded command if applicable
     */
    public String expandDirectionShortcuts(String command) {
        if (command == null) return "";
        
        switch (command.toLowerCase().trim()) {
            case "n":
            case "north":
                return "go north";
            case "s":
            case "south":
                return "go south";
            case "e":
            case "east":
                return "go east";
            case "w":
            case "west":
                return "go west";
            default:
                return command;
        }
    }

    /**
     * Print the help message showing available commands.
     */
    public void printHelp() {
        System.out.println("\n=== WONDERLAND COMMANDS ===");
        System.out.println("Movement:");
        System.out.println("  go <direction>  - Move in a direction (north/south/east/west)");
        System.out.println("  n, s, e, w      - Shortcuts for go north/south/east/west");
        System.out.println();
        System.out.println("Exploration:");
        System.out.println("  look            - Look around the current location");
        System.out.println("  search          - Search for hidden items");
        System.out.println();
        System.out.println("Items:");
        System.out.println("  take <item>     - Pick up an item");
        System.out.println("  drop <item>     - Drop an item");
        System.out.println("  use <item>      - Use an item");
        System.out.println("  inventory (i)   - Show your inventory");
        System.out.println();
        System.out.println("Characters:");
        System.out.println("  talk <name>     - Talk to a character");
        System.out.println();
        System.out.println("Safe Room (Tea Party):");
        System.out.println("  stash <item>    - Store an item safely");
        System.out.println("  unstash <item>  - Retrieve a stashed item");
        System.out.println();
        System.out.println("Other:");
        System.out.println("  hint            - Get a hint about what to do");
        System.out.println("  help            - Show this help message");
        System.out.println("  quit            - Exit the game");
        System.out.println("===========================\n");
    }
}
