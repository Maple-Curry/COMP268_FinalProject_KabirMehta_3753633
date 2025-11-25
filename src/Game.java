import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final int TEA_PARTY = 3; // Tea Party is the safe room (0-indexed)
    private Locations[] map;
    private int currentLocation = 0;
    private Inventory inventory = new Inventory();
    private Control control = new Control();
    private List<Items> allItems = new ArrayList<>();
    private List<Characters> allCharacters = new ArrayList<>();

    // Main game loop
    public void start() {
        loadGameWorld();  // Load from resource files
        
        if (map == null || map.length == 0) {
            System.out.println("Error: Failed to load the game world. Exiting.");
            return;
        }
        
        System.out.println("Welcome to Wonderland! Your goal is to explore and survive.");
        System.out.println("Type 'help' for a list of commands.");

        boolean playing = true;

        while (playing) {
            Locations location = map[currentLocation];
            System.out.println("\n" + location.getDescription(!location.hasBeenVisited()));

            // Get player input
            String command = control.getCommand();
            command = command.replaceAll("[^a-zA-Z0-9\\s]", "").trim().toLowerCase();
            
            if (command.isEmpty()) {
                continue;
            }
            
            String[] parts = command.split(" ", 2);
            String verb = parts[0];
            String remainder = parts.length > 1 ? parts[1] : "";
            
            Actions action = Actions.fromString(verb);

            if (action == null) {
                System.out.println("I don't understand that command. Type 'help' for a list of commands.");
                continue;
            }

            switch (action) {
                case GO:
                    // Handle direction shortcuts (n, s, e, w, north, south, east, west)
                    String direction = remainder;
                    if (verb.equals("n") || verb.equals("north")) direction = "north";
                    else if (verb.equals("s") || verb.equals("south")) direction = "south";
                    else if (verb.equals("e") || verb.equals("east")) direction = "east";
                    else if (verb.equals("w") || verb.equals("west")) direction = "west";
                    
                    if (direction.isEmpty()) {
                        System.out.println("Go where? (north, south, east, west)");
                    } else {
                        int nextRoom = location.getExit(direction);
                        if (nextRoom != -1) {
                            currentLocation = nextRoom;
                        } else {
                            System.out.println("You can't go that way.");
                        }
                    }
                    break;

                case LOOK:
                    System.out.println("You look around...");
                    List<Items> visibleItems = location.getItems();
                    List<Characters> chars = location.getCharacters();
                    if (visibleItems.isEmpty() && chars.isEmpty()) {
                        System.out.println("Nothing else catches your eye.");
                    } else {
                        if (!visibleItems.isEmpty()) {
                            System.out.println("Items here:");
                            visibleItems.forEach(item -> System.out.println("- " + item.getName() + ": " + item.getDescription()));
                        }
                        if (!chars.isEmpty()) {
                            System.out.println("Characters here:");
                            chars.forEach(character -> System.out.println("- " + character.getName() + ": " + character.getDescription()));
                        }
                    }
                    break;

                case TAKE:
                    if (!remainder.isEmpty()) {
                        takeItem(location, remainder);
                    } else {
                        System.out.println("Take what?");
                    }
                    break;

                case DROP:
                    if (!remainder.isEmpty()) {
                        dropItem(location, remainder);
                    } else {
                        System.out.println("Drop what?");
                    }
                    break;

                case INVENTORY:
                    inventory.listItems();
                    break;

                case USE:
                    if (!remainder.isEmpty()) {
                        useItem(remainder);
                    } else {
                        System.out.println("Use what?");
                    }
                    break;

                case TALK:
                    if (!remainder.isEmpty()) {
                        talkToCharacter(location, remainder);
                    } else {
                        System.out.println("Talk to whom?");
                    }
                    break;

                case STASH:
                    if (!remainder.isEmpty()) {
                        stashItem(location, remainder);
                    } else {
                        System.out.println("Stash what?");
                    }
                    break;

                case RETRIEVE:
                    if (!remainder.isEmpty()) {
                        retrieveItem(location, remainder);
                    } else {
                        // List stash if no item specified
                        if (location.isSafeRoom()) {
                            location.listStash();
                        } else {
                            System.out.println("This is not a safe room. Find the Tea Party to access your stash.");
                        }
                    }
                    break;

                case SEARCH:
                    searchLocation(remainder);
                    break;

                case HELP:
                    displayHelp();
                    break;

                case QUIT:
                    playing = false;
                    System.out.println("Goodbye! Thanks for visiting Wonderland!");
                    break;

                default:
                    System.out.println("I don't understand that command.");
            }
        }
    }

    // Handles item pickup from the current room
    private void takeItem(Locations currentRoom, String itemName) {
        Items item = currentRoom.getItem(itemName);
        if (item != null && item.isCollectible()) {
            inventory.addItem(item);
            currentRoom.removeItem(item);
            System.out.println("You picked up: " + item.getName());
        } else if (item != null && !item.isCollectible()) {
            System.out.println("You can't take that.");
        } else {
            System.out.println("That item is not here.");
        }
    }

    // Handles dropping item into the current room
    private void dropItem(Locations currentRoom, String itemName) {
        Items item = inventory.getItemByName(itemName);
        if (item != null) {
            currentRoom.addItem(item);
            inventory.removeItem(item);
            System.out.println("You dropped: " + item.getName());
        } else {
            System.out.println("You don't have that item.");
        }
    }

    // Handle using an item
    private void useItem(String itemName) {
        Items item = inventory.getItemByName(itemName);
        if (item != null) {
            System.out.println("You use the " + item.getName() + ".");
            System.out.println(item.getDescription());
            // TODO: Implement specific item use effects
        } else {
            System.out.println("You don't have that item.");
        }
    }

    // Talk to a character in the room
    private void talkToCharacter(Locations currentRoom, String charName) {
        Characters character = currentRoom.getCharacter(charName);
        if (character != null) {
            character.speak();
        } else {
            System.out.println("There's no one by that name here.");
        }
    }

    // Stash item in the safe room
    private void stashItem(Locations currentRoom, String itemName) {
        if (!currentRoom.isSafeRoom()) {
            System.out.println("This is not a safe room. Find the Tea Party to stash items.");
            return;
        }
        Items item = inventory.getItemByName(itemName);
        if (item != null) {
            if (currentRoom.stashItem(item)) {
                inventory.removeItem(item);
                System.out.println("You stashed: " + item.getName());
            }
        } else {
            System.out.println("You don't have that item.");
        }
    }

    // Retrieve item from the safe room stash
    private void retrieveItem(Locations currentRoom, String itemName) {
        if (!currentRoom.isSafeRoom()) {
            System.out.println("This is not a safe room. Find the Tea Party to retrieve items.");
            return;
        }
        Items item = currentRoom.retrieveItem(itemName);
        if (item != null) {
            inventory.addItem(item);
            System.out.println("You retrieved: " + item.getName());
        } else {
            System.out.println("That item is not in the stash.");
        }
    }

    // Search the current location
    public void searchLocation(String target) {
        Locations location = map[currentLocation];
        System.out.println("You search the area...");
        List<Items> foundItems = location.getItems();
        if (foundItems.isEmpty()) {
            System.out.println("You don't find anything of interest.");
        } else {
            System.out.println("You find:");
            for (Items item : foundItems) {
                if (!item.isHidden()) {
                    System.out.println("- " + item.getName());
                }
            }
        }
    }

    // Display help information
    public void displayHelp() {
        System.out.println("\n+---------------- Commands ----------------+");
        System.out.println("| go [direction] - Move in a direction     |");
        System.out.println("|   (north/n, south/s, east/e, west/w)     |");
        System.out.println("| look - Look around the current location  |");
        System.out.println("| take [item] - Pick up an item            |");
        System.out.println("| drop [item] - Drop an item               |");
        System.out.println("| use [item] - Use an item in your inventory|");
        System.out.println("| talk [character] - Talk to a character   |");
        System.out.println("| inventory (i) - View your inventory      |");
        System.out.println("| stash [item] - Store item in safe room   |");
        System.out.println("| retrieve [item] - Get item from stash    |");
        System.out.println("| search - Search the current location     |");
        System.out.println("| help - Display this help message         |");
        System.out.println("| quit - Exit the game                     |");
        System.out.println("+------------------------------------------+");
    }

    // Display hint (placeholder)
    public void displayHint() {
        System.out.println("Hint: Explore carefully and talk to everyone you meet!");
        // TODO: Load hints from hints.txt
    }

    // Get the inventory object
    public Inventory getInventory() {
        return inventory;
    }

    // Load game world from resource files
    private void loadGameWorld() {
        loadLocations();
        loadItems();
        loadCharacters();
        
        // Mark the Tea Party location as the designated safe room
        if (TEA_PARTY >= 0 && map != null && TEA_PARTY < map.length) {
            map[TEA_PARTY].setSafeRoom(true);
            System.out.println("The Tea Party has been designated as your safe room for stashing items.");
        }
    }

    private void loadLocations() {
        try {
            List<String> locationLines = Files.readAllLines(Paths.get("Resource/locations.txt"));
            map = new Locations[locationLines.size()];

            for (int i = 0; i < locationLines.size(); i++) {
                String line = locationLines.get(i);
                Locations loc = Locations.fromResourceLine(i, line);
                if (loc != null) {
                    map[i] = loc;
                }
            }

            System.out.println("Loaded " + map.length + " locations.");

        } catch (IOException e) {
            // Try alternate path
            try {
                List<String> locationLines = Files.readAllLines(Paths.get("src/Resource/locations.txt"));
                map = new Locations[locationLines.size()];

                for (int i = 0; i < locationLines.size(); i++) {
                    String line = locationLines.get(i);
                    Locations loc = Locations.fromResourceLine(i, line);
                    if (loc != null) {
                        map[i] = loc;
                    }
                }

                System.out.println("Loaded " + map.length + " locations.");
            } catch (IOException ex) {
                System.out.println("Error loading locations.txt: " + ex.getMessage());
            }
        }
    }

    private void loadItems() {
        try {
            List<String> itemLines = Files.readAllLines(Paths.get("Resource/items.txt"));
            
            for (String line : itemLines) {
                Items item = Items.fromResourceLine(line);
                if (item != null) {
                    allItems.add(item);
                    // Place item in its default location
                    int locId = item.getDefaultLocationId();
                    if (locId >= 0 && map != null && locId < map.length && map[locId] != null) {
                        map[locId].addItem(item);
                    }
                }
            }

            System.out.println("Loaded " + allItems.size() + " items.");

        } catch (IOException e) {
            // Try alternate path
            try {
                List<String> itemLines = Files.readAllLines(Paths.get("src/Resource/items.txt"));
                
                for (String line : itemLines) {
                    Items item = Items.fromResourceLine(line);
                    if (item != null) {
                        allItems.add(item);
                        int locId = item.getDefaultLocationId();
                        if (locId >= 0 && map != null && locId < map.length && map[locId] != null) {
                            map[locId].addItem(item);
                        }
                    }
                }

                System.out.println("Loaded " + allItems.size() + " items.");
            } catch (IOException ex) {
                System.out.println("Error loading items.txt: " + ex.getMessage());
            }
        }
    }

    private void loadCharacters() {
        try {
            List<String> charLines = Files.readAllLines(Paths.get("Resource/characters.txt"));
            
            for (String line : charLines) {
                Characters character = Characters.fromResourceLine(line);
                if (character != null) {
                    allCharacters.add(character);
                    // Place character in their default location
                    int locId = character.getDefaultLocationId();
                    if (locId >= 0 && map != null && locId < map.length && map[locId] != null) {
                        map[locId].addCharacter(character);
                    }
                }
            }

            System.out.println("Loaded " + allCharacters.size() + " characters.");

        } catch (IOException e) {
            // Try alternate path
            try {
                List<String> charLines = Files.readAllLines(Paths.get("src/Resource/characters.txt"));
                
                for (String line : charLines) {
                    Characters character = Characters.fromResourceLine(line);
                    if (character != null) {
                        allCharacters.add(character);
                        int locId = character.getDefaultLocationId();
                        if (locId >= 0 && map != null && locId < map.length && map[locId] != null) {
                            map[locId].addCharacter(character);
                        }
                    }
                }

                System.out.println("Loaded " + allCharacters.size() + " characters.");
            } catch (IOException ex) {
                System.out.println("Error loading characters.txt: " + ex.getMessage());
            }
        }
    }

    // TODO: Implement save game functionality
    // public void saveGame(String filename) { ... }

    // TODO: Implement load game functionality
    // public void loadGame(String filename) { ... }
}
