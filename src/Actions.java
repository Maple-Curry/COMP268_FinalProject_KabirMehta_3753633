/**
 * Actions.java - Enumeration of all possible game actions.
 * Provides string parsing with aliases for flexible command recognition.
 */
public enum Actions {
    GO,
    LOOK,
    TAKE,
    DROP,
    INVENTORY,
    USE,
    TALK,
    SEARCH,
    STASH,
    UNSTASH,
    HINT,
    HELP,
    QUIT;

    /**
     * Parse a string into an Actions enum value.
     * Supports various aliases and abbreviations.
     * @param input The user input to parse
     * @return The matching Actions enum, or null if not recognized
     */
    public static Actions fromString(String input) {
        if (input == null) return null;
        
        switch (input.toLowerCase().trim()) {
            // Movement
            case "go":
            case "move":
            case "walk":
            case "north": case "n":
            case "south": case "s":
            case "east": case "e":
            case "west": case "w":
                return GO;
            
            // Looking around
            case "look":
            case "l":
            case "examine":
            case "inspect":
                return LOOK;
            
            // Taking items
            case "take":
            case "get":
            case "grab":
            case "pick":
            case "pickup":
                return TAKE;
            
            // Dropping items
            case "drop":
            case "put":
            case "leave":
                return DROP;
            
            // Inventory
            case "inventory":
            case "inv":
            case "i":
            case "items":
            case "bag":
                return INVENTORY;
            
            // Using items
            case "use":
            case "apply":
            case "activate":
                return USE;
            
            // Talking to characters
            case "talk":
            case "speak":
            case "chat":
            case "ask":
            case "greet":
                return TALK;
            
            // Searching the area
            case "search":
            case "find":
            case "explore":
                return SEARCH;
            
            // Safe room stashing
            case "stash":
            case "store":
            case "hide":
                return STASH;
            
            case "unstash":
            case "retrieve":
            case "unstore":
                return UNSTASH;
            
            // Hints
            case "hint":
            case "clue":
            case "tips":
                return HINT;
            
            // Help
            case "help":
            case "h":
            case "?":
            case "commands":
                return HELP;
            
            // Quitting
            case "quit":
            case "exit":
            case "q":
            case "bye":
                return QUIT;
            
            default:
                return null;
        }
    }
}
