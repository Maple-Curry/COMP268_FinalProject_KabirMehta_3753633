import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Control {
    private Scanner scanner = new Scanner(System.in);
    private Map<String, String> aliasMap = new HashMap<>();

    public Control() {
        // Initialize command aliases
        aliasMap.put("n", "go north");
        aliasMap.put("s", "go south");
        aliasMap.put("e", "go east");
        aliasMap.put("w", "go west");
        aliasMap.put("north", "go north");
        aliasMap.put("south", "go south");
        aliasMap.put("east", "go east");
        aliasMap.put("west", "go west");
        aliasMap.put("i", "inventory");
        aliasMap.put("inv", "inventory");
        aliasMap.put("l", "look");
        aliasMap.put("h", "help");
        aliasMap.put("?", "help");
        aliasMap.put("q", "quit");
        aliasMap.put("get", "take");
        aliasMap.put("pick", "take");
        aliasMap.put("put", "drop");
        aliasMap.put("speak", "talk");
        aliasMap.put("store", "stash");
        aliasMap.put("retrieve", "unstash");
        aliasMap.put("examine", "look");
    }

    // Clean and normalize user input
    public String cleanInput(String input) {
        if (input == null) {
            return "";
        }
        // Trim, lowercase, and remove extra whitespace
        return input.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    // Apply alias normalization
    public String normalizeCommand(String input) {
        String cleaned = cleanInput(input);
        if (cleaned.isEmpty()) {
            return "";
        }
        
        // Check if the entire input is an alias
        if (aliasMap.containsKey(cleaned)) {
            return aliasMap.get(cleaned);
        }
        
        // Check if the first word is an alias that maps to a multi-word command
        String[] parts = cleaned.split(" ", 2);
        String firstWord = parts[0];
        
        // For simple aliases like "get item" -> "take item"
        if (aliasMap.containsKey(firstWord) && !aliasMap.get(firstWord).contains(" ")) {
            return parts.length > 1 ? aliasMap.get(firstWord) + " " + parts[1] : aliasMap.get(firstWord);
        }
        
        return cleaned;
    }

    public String getCommand() {
        System.out.print("> ");
        String input = scanner.nextLine();
        return normalizeCommand(input);
    }

    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  go [direction] or n/s/e/w - Move in a direction");
        System.out.println("  look or l                 - Look around the room");
        System.out.println("  take [item] or get        - Pick up an item");
        System.out.println("  drop [item] or put        - Drop an item");
        System.out.println("  use [item]                - Use an item");
        System.out.println("  talk [character]          - Talk to a character");
        System.out.println("  inventory or i            - Show your inventory");
        System.out.println("  stash [item]              - Store item in safe room (Tea Party only)");
        System.out.println("  unstash [item]            - Retrieve item from stash");
        System.out.println("  save                      - Save the game (coming soon)");
        System.out.println("  load                      - Load a saved game (coming soon)");
        System.out.println("  help or h or ?            - Show this help");
        System.out.println("  quit or q                 - Quit the game");
    }
}

