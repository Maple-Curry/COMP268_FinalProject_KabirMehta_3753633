import java.util.ArrayList;

public class Locations {
    private String name;
    private String description;
    private int[] exits = new int[4]; // N=0, S=1, E=2, W=3
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Characters> characters = new ArrayList<>();
    private boolean visited = false;

    // Constructor to initialize name, description, and exits
    public Locations(String name, String description, int[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
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
}
