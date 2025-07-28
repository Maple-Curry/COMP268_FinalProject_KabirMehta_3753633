import java.util.ArrayList;

public class Locations {
    private String name;
    private String description;
    private int[] exits = new int[4]; // N=0, S=1, E=2, W=3
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Characters> characters = new ArrayList<>();
    private boolean visited = false;

    public Locations(String name, String description, int[] exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }

    public String getName() {
        return name;
    }

    public String getDescription(boolean firstVisit) {
        visited = true;
        return firstVisit ? description : "You are at " + name + " again.";
    }

    public int getExit(String direction) {
        switch (direction.toLowerCase()) {
            case "north": case "n": return exits[0];
            case "south": case "s": return exits[1];
            case "east":  case "e": return exits[2];
            case "west":  case "w": return exits[3];
            default: return -1;
        }
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void addItem(Items item) {
        items.add(item);
    }

    public void removeItem(Items item) {
        items.remove(item);
    }

    public void addCharacter(Characters character) {
        characters.add(character);
    }

    public ArrayList<Characters> getCharacters() {
        return characters;
    }

    public ArrayList<Items> getItems() {
        return itemsInRoom;
    }

    public Items getItem (String itemName) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}

