import java.util.ArrayList;
import java.util.List;

public class Locations {
    private String name;
    private String description;
    private int[] exits = new int[4]; // N=0, S=1, E=2, W=3
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Characters> characters = new ArrayList<>();
    private boolean visited = false;
    private boolean isSafeRoom = false;
    private ArrayList<Items> stash = new ArrayList<>();

    // Constructor to initialize name, description, and exits
    public Locations(String name, String description, int[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    /**
     * Parse a location from a resource file line.
     * Format: name|description|exit_n,exit_s,exit_e,exit_w
     */
    public static Locations fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Location line cannot be null or empty");
        }
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Location line must have 3 fields: " + line);
        }
        String name = parts[0].trim();
        String description = parts[1].trim();
        String[] exitStrings = parts[2].split(",");
        if (exitStrings.length < 4) {
            throw new IllegalArgumentException("Location must have 4 exits: " + line);
        }
        int[] exits = new int[4];
        for (int i = 0; i < 4; i++) {
            exits[i] = Integer.parseInt(exitStrings[i].trim());
        }
        return new Locations(name, description, exits);
    }

    // Return location name
    public String getName() {
        return name;
    }

    public boolean hasBeenVisited() {
        return visited;
    }

    // Return description based on whether it's the first visit
    public String getDescription(boolean firstVisit) {
        visited = true;
        return firstVisit ? description : "You are at " + name + " again.";
    }

    // Get the index of the next room based on direction input
    public int getExit(String direction) {
        switch (direction.toLowerCase()) {
            case "north": case "n": return exits[0];
            case "south": case "s": return exits[1];
            case "east":  case "e": return exits[2];
            case "west":  case "w": return exits[3];
            default: return -1;
        }
    }

    // Return the list of items currently in the room
    public ArrayList<Items> getItems() {
        return items;
    }

    // Get visible items (non-hidden)
    public List<Items> getVisibleItems() {
        List<Items> visible = new ArrayList<>();
        for (Items item : items) {
            if (!item.isHidden()) {
                visible.add(item);
            }
        }
        return visible;
    }

    // Add an item to the room
    public void addItem(Items item) {
        items.add(item);
    }

    // Remove an item from the room
    public void removeItem(Items item) {
        items.remove(item);
    }

    // Add a character to the room
    public void addCharacter(Characters character) {
        characters.add(character);
    }

    // Return list of characters in the room
    public ArrayList<Characters> getCharacters() {
        return characters;
    }

    // Find and return an item by name from the room
    public Items getItem(String itemName) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Optional: Find and return a character by name from the room
    public Characters getCharacter(String name) {
        for (Characters character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }

    // Safe room support
    public boolean isSafeRoom() {
        return isSafeRoom;
    }

    public void setSafeRoom(boolean safeRoom) {
        isSafeRoom = safeRoom;
    }

    // Stash methods for safe room storage
    public void addToStash(Items item) {
        stash.add(item);
    }

    public void removeFromStash(Items item) {
        stash.remove(item);
    }

    public Items getFromStash(String itemName) {
        for (Items item : stash) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public List<Items> getStash() {
        return new ArrayList<>(stash);
    }

    public boolean hasStash() {
        return !stash.isEmpty();
    }

    // Build suggestions for what player can do here
    public String getSuggestions() {
        StringBuilder sb = new StringBuilder();
        if (!getVisibleItems().isEmpty()) {
            sb.append("There are items you can take. ");
        }
        if (!characters.isEmpty()) {
            sb.append("Someone is here you might talk to. ");
        }
        if (isSafeRoom && hasStash()) {
            sb.append("Your stash contains items. ");
        }
        return sb.toString().trim();
    }
}
