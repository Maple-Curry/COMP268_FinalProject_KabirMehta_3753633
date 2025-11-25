import java.util.ArrayList;
import java.util.List;

/**
 * Locations.java - Represents a location in the Wonderland text adventure.
 * Locations have exits, items, characters, and optionally a stash for safe storage.
 */
public class Locations {
    private int id;
    private String name;
    private String description;
    private int[] exits = new int[4]; // N=0, S=1, E=2, W=3
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Characters> characters = new ArrayList<>();
    private ArrayList<Items> stash = new ArrayList<>(); // Safe room stash
    private boolean visited = false;
    private boolean isSafeRoom = false;
    private boolean searched = false;

    /**
     * Constructor with ID, name, description, and exits.
     */
    public Locations(int id, String name, String description, int[] exits) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    /**
     * Constructor without ID (ID defaults to -1).
     */
    public Locations(String name, String description, int[] exits) {
        this(-1, name, description, exits);
    }

    /**
     * Parse a location from a resource file line.
     * Format: name|description|n,s,e,w
     * @param line The line from the resource file
     * @param id The location ID (line number)
     * @return A new Locations object, or null if parsing fails
     */
    public static Locations fromResourceLine(String line, int id) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            System.out.println("Warning: Invalid location line: " + line);
            return null;
        }
        try {
            String name = parts[0].trim();
            String description = parts[1].trim();
            String[] exitStrings = parts[2].split(",");
            int[] exits = new int[4];
            for (int j = 0; j < 4; j++) {
                exits[j] = Integer.parseInt(exitStrings[j].trim());
            }
            return new Locations(id, name, description, exits);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Warning: Could not parse location exits: " + line);
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasBeenVisited() {
        return visited;
    }

    public boolean hasBeenSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isSafeRoom() {
        return isSafeRoom;
    }

    public void setSafeRoom(boolean safeRoom) {
        this.isSafeRoom = safeRoom;
    }

    /**
     * Return description based on whether it's the first visit.
     */
    public String getDescription(boolean firstVisit) {
        visited = true;
        StringBuilder sb = new StringBuilder();
        if (firstVisit) {
            sb.append(description);
        } else {
            sb.append("You are at ").append(name).append(" again.");
        }
        
        // Show available exits
        sb.append("\nExits: ");
        List<String> exitList = new ArrayList<>();
        if (exits[0] != -1) exitList.add("north");
        if (exits[1] != -1) exitList.add("south");
        if (exits[2] != -1) exitList.add("east");
        if (exits[3] != -1) exitList.add("west");
        sb.append(exitList.isEmpty() ? "none" : String.join(", ", exitList));
        
        return sb.toString();
    }

    /**
     * Get the index of the next room based on direction input.
     */
    public int getExit(String direction) {
        switch (direction.toLowerCase()) {
            case "north": case "n": return exits[0];
            case "south": case "s": return exits[1];
            case "east":  case "e": return exits[2];
            case "west":  case "w": return exits[3];
            default: return -1;
        }
    }

    /**
     * Get the exits array.
     */
    public int[] getExits() {
        return exits;
    }

    /**
     * Count the number of valid exits.
     */
    public int getExitCount() {
        int count = 0;
        for (int exit : exits) {
            if (exit != -1) count++;
        }
        return count;
    }

    // Item management
    public ArrayList<Items> getItems() {
        return items;
    }

    /**
     * Get visible items (non-hidden).
     */
    public List<Items> getVisibleItems() {
        List<Items> visible = new ArrayList<>();
        for (Items item : items) {
            if (!item.isHidden()) {
                visible.add(item);
            }
        }
        return visible;
    }

    public void addItem(Items item) {
        items.add(item);
    }

    public void removeItem(Items item) {
        items.remove(item);
    }

    public Items getItem(String itemName) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Character management
    public void addCharacter(Characters character) {
        characters.add(character);
    }

    public ArrayList<Characters> getCharacters() {
        return characters;
    }

    public Characters getCharacter(String name) {
        for (Characters character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }

    // Safe room stash methods
    /**
     * Stash an item in this location's safe storage.
     * @param item The item to stash
     * @return true if stashing succeeded (only in safe rooms)
     */
    public boolean stashItem(Items item) {
        if (!isSafeRoom) {
            return false;
        }
        stash.add(item);
        return true;
    }

    /**
     * Take an item from the stash.
     * @param itemName The name of the item to take
     * @return The item, or null if not found
     */
    public Items takeFromStash(String itemName) {
        for (Items item : stash) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                stash.remove(item);
                return item;
            }
        }
        return null;
    }

    /**
     * Get a list of stashed items.
     */
    public List<Items> getStash() {
        return new ArrayList<>(stash);
    }

    /**
     * Build a list of suggested commands for this location.
     */
    public String buildSuggestedCommands(Inventory inventory) {
        StringBuilder sb = new StringBuilder("Suggested commands: look, inventory, help");
        
        // Add directional suggestions
        if (exits[0] != -1) sb.append(", go north");
        if (exits[1] != -1) sb.append(", go south");
        if (exits[2] != -1) sb.append(", go east");
        if (exits[3] != -1) sb.append(", go west");
        
        // Add item interactions
        if (!getVisibleItems().isEmpty()) {
            sb.append(", take <item>");
        }
        if (!inventory.isEmpty()) {
            sb.append(", drop <item>");
        }
        
        // Add character interactions
        if (!characters.isEmpty()) {
            sb.append(", talk <character>");
        }
        
        // Add safe room commands
        if (isSafeRoom) {
            sb.append(", stash <item>, unstash <item>");
        }
        
        // Add search if not searched
        if (!searched) {
            sb.append(", search");
        }
        
        return sb.toString();
    }
}
