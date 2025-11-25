import java.util.ArrayList;
import java.util.List;

public class Locations {
    private String name;
    private String description;
    private int[] exits = new int[4]; // N=0, S=1, E=2, W=3
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Characters> characters = new ArrayList<>();
    private boolean visited = false;
    private boolean safeRoom = false;
    private final List<Items> stash = new ArrayList<>();

    // Constructor to initialize name, description, and exits
    public Locations(String name, String description, int[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    public static Locations fromResourceLine(int id, String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            System.out.println("Invalid location line: " + line);
            return null;
        }
        try {
            String locName = parts[0].trim();
            String locDescription = parts[1].trim();
            String[] exitStrings = parts[2].split(",");
            int[] exits = new int[4];
            for (int j = 0; j < 4 && j < exitStrings.length; j++) {
                exits[j] = Integer.parseInt(exitStrings[j].trim());
            }
            return new Locations(locName, locDescription, exits);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing location data: " + e.getMessage());
            return null;
        }
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

    // Safe room functionality
    public boolean isSafeRoom() {
        return safeRoom;
    }

    public void setSafeRoom(boolean safeRoom) {
        this.safeRoom = safeRoom;
    }

    public boolean stashItem(Items item) {
        if (!safeRoom || item == null) {
            return false;
        }
        stash.add(item);
        return true;
    }

    public Items retrieveItem(String itemName) {
        if (!safeRoom) {
            return null;
        }
        Items foundItem = null;
        for (Items item : stash) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                foundItem = item;
                break;
            }
        }
        if (foundItem != null) {
            stash.remove(foundItem);
        }
        return foundItem;
    }

    public List<Items> getStash() {
        return stash;
    }

    public void listStash() {
        if (!safeRoom) {
            System.out.println("This is not a safe room.");
            return;
        }
        if (stash.isEmpty()) {
            System.out.println("The stash is empty.");
        } else {
            System.out.println("Items in the stash:");
            for (Items item : stash) {
                System.out.println("- " + item.getName());
            }
        }
    }
}
