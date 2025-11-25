import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Locations[] map;
    private int currentLocation = 0;
    private Inventory inventory = new Inventory();
    private Control control = new Control();
    
    // Safe room location index (Tea Party)
    private static final int TEA_PARTY = 3;

    // Main game loop
    public void start() {
        loadGameWorld();
        if (map == null || map.length == 0) {
            System.out.println("Failed to load game world. Exiting.");
            return;
        }
        
        System.out.println("Welcome to Wonderland! Your goal is to explore and survive.");
        System.out.println("Type 'help' for a list of commands.\n");

        boolean playing = true;

        while (playing) {
            Locations location = map[currentLocation];
            System.out.println("\n" + location.getDescription(!location.hasBeenVisited()));
            
            // Trigger location enter events
            GameEventSystem.onLocationEnter(location, inventory);

            // Get player input
            String command = control.getCommand();
            String[] parts = command.split(" ", 2);
            Actions action = Actions.fromString(parts[0]);

            if (action == null) {
                System.out.println("I don't understand that command. Type 'help' for options.");
                continue;
            }

            switch (action) {
                case GO:
                    if (parts.length < 2) {
                        System.out.println("Go where? (north/south/east/west)");
                    } else {
                        int nextRoom = location.getExit(parts[1]);
                        if (nextRoom != -1) {
                            currentLocation = nextRoom;
                        } else {
                            System.out.println("You can't go that way.");
                        }
                    }
                    break;

                case LOOK:
                    lookAround(location);
                    break;

                case TAKE:
                    if (parts.length > 1) {
                        takeItem(location, parts[1]);
                    } else {
                        System.out.println("Take what?");
                    }
                    break;

                case DROP:
                    if (parts.length > 1) {
                        dropItem(location, parts[1]);
                    } else {
                        System.out.println("Drop what?");
                    }
                    break;

                case INVENTORY:
                    showInventory();
                    break;

                case USE:
                    if (parts.length > 1) {
                        useItem(parts[1]);
                    } else {
                        System.out.println("Use what?");
                    }
                    break;

                case TALK:
                    if (parts.length > 1) {
                        talkTo(location, parts[1]);
                    } else {
                        System.out.println("Talk to whom?");
                    }
                    break;

                case SEARCH:
                    GameEventSystem.onSearch(location);
                    break;

                case STASH:
                    if (parts.length > 1) {
                        stashItem(location, parts[1]);
                    } else {
                        System.out.println("Stash what?");
                    }
                    break;

                case RETRIEVE:
                    if (parts.length > 1) {
                        retrieveItem(location, parts[1]);
                    } else {
                        listStash(location);
                    }
                    break;

                case HINT:
                    control.printHint(location, inventory);
                    break;

                case HELP:
                    control.printHelp();
                    break;

                case QUIT:
                    playing = false;
                    System.out.println("Goodbye! Thanks for playing Wonderland.");
                    break;

                default:
                    System.out.println("I don't understand that command.");
            }
        }
    }

    /**
     * Show inventory contents - called by Control delegation or INVENTORY action.
     */
    public void showInventory() {
        inventory.listItems();
    }

    /**
     * Look around the current location.
     */
    private void lookAround(Locations location) {
        System.out.println("You are at: " + location.getName());
        
        // Show visible items
        List<Items> visibleItems = location.getVisibleItems();
        if (!visibleItems.isEmpty()) {
            System.out.println("You see:");
            for (Items item : visibleItems) {
                System.out.println("  - " + item.getName());
            }
        }
        
        // Show characters
        ArrayList<Characters> characters = location.getCharacters();
        if (!characters.isEmpty()) {
            System.out.println("Present here:");
            for (Characters character : characters) {
                System.out.println("  - " + character.getName());
            }
        }
        
        // Show stash info if safe room
        if (location.isSafeRoom() && location.hasStash()) {
            System.out.println("Your stash contains " + location.getStash().size() + " item(s).");
        }
    }

    // Handles item pickup from the current room
    private void takeItem(Locations currentRoom, String itemName) {
        Items item = currentRoom.getItem(itemName);
        if (item != null && !item.isHidden() && item.isCollectible()) {
            inventory.addItem(item);
            currentRoom.removeItem(item);
            System.out.println("You picked up: " + item.getName());
            GameEventSystem.onItemPickup(item, currentRoom, inventory);
        } else if (item != null && item.isHidden()) {
            System.out.println("That item is not here.");
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

    // Use an item from inventory
    private void useItem(String itemName) {
        Items item = inventory.getItemByName(itemName);
        if (item != null) {
            GameEventSystem.onItemUse(item, map[currentLocation], inventory);
        } else {
            System.out.println("You don't have that item.");
        }
    }

    // Talk to a character in the current location
    private void talkTo(Locations location, String characterName) {
        Characters character = location.getCharacter(characterName);
        if (character != null) {
            character.speak();
        } else {
            System.out.println("There's no one here by that name.");
        }
    }

    // Stash an item in the safe room
    private void stashItem(Locations location, String itemName) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only stash items in the safe room (Tea Party).");
            return;
        }
        Items item = inventory.getItemByName(itemName);
        if (item != null) {
            location.addToStash(item);
            inventory.removeItem(item);
            System.out.println("You stashed: " + item.getName());
        } else {
            System.out.println("You don't have that item.");
        }
    }

    // Retrieve an item from the safe room stash
    private void retrieveItem(Locations location, String itemName) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only retrieve items from the safe room (Tea Party).");
            return;
        }
        Items item = location.getFromStash(itemName);
        if (item != null) {
            inventory.addItem(item);
            location.removeFromStash(item);
            System.out.println("You retrieved: " + item.getName());
        } else {
            System.out.println("That item is not in your stash.");
        }
    }

    // List stash contents
    private void listStash(Locations location) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only access your stash in the safe room (Tea Party).");
            return;
        }
        List<Items> stash = location.getStash();
        if (stash.isEmpty()) {
            System.out.println("Your stash is empty.");
        } else {
            System.out.println("Stash contents:");
            for (Items item : stash) {
                System.out.println("  - " + item.getName());
            }
        }
    }

    // Load game world from resource files
    private void loadGameWorld() {
        try {
            loadLocations();
            loadItems();
            loadCharacters();
            
            // Set Tea Party as safe room
            if (map.length > TEA_PARTY) {
                map[TEA_PARTY].setSafeRoom(true);
            }
            
            System.out.println("Game world loaded with " + map.length + " locations.");

        } catch (Exception e) {
            System.out.println("Error loading game world: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLocations() throws IOException {
        List<String> lines = readResourceFile("Resource/locations.txt");
        map = new Locations[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            map[i] = Locations.fromResourceLine(lines.get(i));
        }
    }

    private void loadItems() throws IOException {
        List<String> lines = readResourceFile("Resource/items.txt");
        for (String line : lines) {
            Items item = Items.fromResourceLine(line);
            int locId = item.getLocationId();
            if (locId >= 0 && locId < map.length) {
                map[locId].addItem(item);
            }
        }
    }

    private void loadCharacters() throws IOException {
        List<String> lines = readResourceFile("Resource/characters.txt");
        for (String line : lines) {
            Characters character = Characters.fromResourceLine(line);
            int locId = character.getLocationId();
            if (locId >= 0 && locId < map.length) {
                map[locId].addCharacter(character);
            }
        }
    }

    /**
     * Read a resource file from the classpath or relative path.
     */
    private List<String> readResourceFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // Try classpath first
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        
        // If not found in classpath, try relative path
        if (is == null) {
            is = getClass().getResourceAsStream("/" + filename);
        }
        
        // If still not found, try as file path relative to current directory
        if (is == null) {
            java.io.File file = new java.io.File(filename);
            if (file.exists()) {
                is = new java.io.FileInputStream(file);
            }
        }
        
        if (is == null) {
            throw new IOException("Could not find resource: " + filename);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        
        return lines;
    }

    // TODO: Save game state to file
    // public void saveGame(String filename) {
    //     // Save current location, inventory, visited locations, stash contents
    // }

    // TODO: Load game state from file
    // public void loadGame(String filename) {
    //     // Restore game state from saved file
    // }
}
