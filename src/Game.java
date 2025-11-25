import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game.java - Main game logic for the Wonderland text adventure.
 * Handles game state, command processing, and resource loading.
 */
public class Game {
    // Game state
    private Locations[] map;
    private List<Items> allItems = new ArrayList<>();
    private List<Characters> allCharacters = new ArrayList<>();
    private int currentLocation = 0;
    private Inventory inventory = new Inventory();
    private Control control = new Control();
    private Random random = new Random();
    
    // Constants
    private static final int TEA_PARTY_LOCATION = 3; // Safe room index
    private static final int ENCOUNTER_THRESHOLD = 3; // Items to trigger encounters
    private static final int ENCOUNTER_CHANCE_DENOMINATOR = 10; // 1 in 10 base chance
    private static final int ENCOUNTER_CHANCE_NUMERATOR = 3;    // 30% chance when threshold met
    private static final String RESOURCE_BASE_PATH = "src/"; // Base path for resource files
    
    // Game flags
    private boolean gameWon = false;
    
    // Hints for the player
    private String[] hints = {
        "Try exploring all the rooms. Some have more exits than others.",
        "The Tea Party is a safe place to stash items you want to keep.",
        "Talk to the characters - they might give you useful information.",
        "Use 'search' in each room to find hidden items.",
        "Carrying too many items might attract unwanted attention...",
        "The small key might open something in the Hall of Doors.",
        "Some items can be used together. Try 'use <item>' when you have ideas.",
        "The Queen's Castle is your goal. Find a way to reach it safely.",
        "The Cheshire Cat speaks in riddles, but there's wisdom in his words."
    };

    /**
     * Main game loop.
     */
    public void start() {
        loadGameWorld();
        
        if (map == null || map.length == 0) {
            System.out.println("Error: Could not load game world. Exiting.");
            return;
        }
        
        printWelcome();
        
        boolean playing = true;
        while (playing && !gameWon) {
            Locations location = map[currentLocation];
            System.out.println("\n" + location.getDescription(!location.hasBeenVisited()));
            
            // Check for random encounters based on inventory
            checkRandomEncounter();
            
            // Get and process player command
            String command = control.getCommand();
            command = control.expandDirectionShortcuts(command);
            
            if (command.isEmpty()) {
                System.out.println("Please enter a command. Type 'help' for a list of commands.");
                continue;
            }
            
            String[] parts = command.split(" ", 2);
            Actions action = Actions.fromString(parts[0]);
            String argument = parts.length > 1 ? parts[1].trim() : "";

            if (action == null) {
                System.out.println("I don't understand '" + parts[0] + "'. Type 'help' for commands.");
                continue;
            }

            switch (action) {
                case GO:
                    handleGo(argument);
                    break;
                    
                case LOOK:
                    handleLook();
                    break;
                    
                case TAKE:
                    if (argument.isEmpty()) {
                        System.out.println("Take what? Try 'take <item name>'");
                    } else {
                        takeItem(argument);
                    }
                    break;
                    
                case DROP:
                    if (argument.isEmpty()) {
                        System.out.println("Drop what? Try 'drop <item name>'");
                    } else {
                        dropItem(argument);
                    }
                    break;
                    
                case INVENTORY:
                    showInventory();
                    break;
                    
                case USE:
                    if (argument.isEmpty()) {
                        System.out.println("Use what? Try 'use <item name>'");
                    } else {
                        useItem(argument);
                    }
                    break;
                    
                case TALK:
                    if (argument.isEmpty()) {
                        System.out.println("Talk to whom? Try 'talk <character name>'");
                    } else {
                        talkToCharacter(argument);
                    }
                    break;
                    
                case SEARCH:
                    searchLocation();
                    break;
                    
                case STASH:
                    if (argument.isEmpty()) {
                        System.out.println("Stash what? Try 'stash <item name>'");
                    } else {
                        stashItem(argument);
                    }
                    break;
                    
                case UNSTASH:
                    if (argument.isEmpty()) {
                        System.out.println("Unstash what? Try 'unstash <item name>'");
                        listStash();
                    } else {
                        unstashItem(argument);
                    }
                    break;
                    
                case HINT:
                    displayHint();
                    break;
                    
                case HELP:
                    displayHelp();
                    break;
                    
                case QUIT:
                    playing = false;
                    System.out.println("\nThank you for playing Wonderland! Goodbye!");
                    break;
                    
                default:
                    System.out.println("I don't understand that command.");
            }
        }
        
        if (gameWon) {
            printVictory();
        }
    }

    // ============ Command Handlers ============

    /**
     * Handle movement commands.
     */
    private void handleGo(String direction) {
        if (direction.isEmpty()) {
            System.out.println("Go where? Try 'go north', 'go south', 'go east', or 'go west'");
            return;
        }
        
        Locations location = map[currentLocation];
        int nextRoom = location.getExit(direction);
        
        if (nextRoom != -1) {
            currentLocation = nextRoom;
            System.out.println("You go " + direction + ".");
        } else {
            System.out.println("You can't go that way.");
        }
    }

    /**
     * Handle look command - show room details.
     */
    private void handleLook() {
        Locations location = map[currentLocation];
        
        System.out.println("\n=== " + location.getName() + " ===");
        
        // Show visible items
        List<Items> visibleItems = location.getVisibleItems();
        if (!visibleItems.isEmpty()) {
            System.out.println("\nYou see the following items:");
            for (Items item : visibleItems) {
                System.out.println("  - " + item.getName() + ": " + item.getDescription());
            }
        }
        
        // Show characters
        ArrayList<Characters> chars = location.getCharacters();
        if (!chars.isEmpty()) {
            System.out.println("\nCharacters here:");
            for (Characters ch : chars) {
                System.out.println("  - " + ch.getName() + ": " + ch.getDescription());
            }
        }
        
        // Show stash if in safe room
        if (location.isSafeRoom()) {
            List<Items> stash = location.getStash();
            System.out.println("\n[This is a safe room. You can stash items here.]");
            if (!stash.isEmpty()) {
                System.out.println("Stashed items:");
                for (Items item : stash) {
                    System.out.println("  - " + item.getName());
                }
            }
        }
        
        // Show suggested commands
        System.out.println("\n" + location.buildSuggestedCommands(inventory));
    }

    /**
     * Take an item from the current room.
     */
    private void takeItem(String itemName) {
        Locations location = map[currentLocation];
        Items item = location.getItem(itemName);
        
        if (item == null) {
            System.out.println("There's no '" + itemName + "' here.");
            return;
        }
        
        if (item.isHidden()) {
            System.out.println("You don't see anything like that here. Try searching first.");
            return;
        }
        
        if (!item.isCollectible()) {
            System.out.println("You can't take the " + item.getName() + ".");
            return;
        }
        
        inventory.addItem(item);
        location.removeItem(item);
        System.out.println("You pick up the " + item.getName() + ".");
    }

    /**
     * Drop an item into the current room.
     */
    private void dropItem(String itemName) {
        Locations location = map[currentLocation];
        Items item = inventory.getItemByName(itemName);
        
        if (item == null) {
            System.out.println("You're not carrying a '" + itemName + "'.");
            return;
        }
        
        location.addItem(item);
        inventory.removeItem(item);
        System.out.println("You drop the " + item.getName() + ".");
    }

    /**
     * Show player inventory.
     */
    private void showInventory() {
        inventory.listItems();
    }

    /**
     * Use an item from inventory.
     */
    private void useItem(String itemName) {
        Items item = inventory.getItemByName(itemName);
        
        if (item == null) {
            System.out.println("You're not carrying a '" + itemName + "'.");
            return;
        }
        
        // Special item uses based on location and item
        Locations location = map[currentLocation];
        String itemLower = item.getName().toLowerCase();
        
        switch (itemLower) {
            case "small key":
                if (currentLocation == 1) { // Hall of Doors
                    System.out.println("You use the small key on the tiny keyhole behind the curtain.");
                    System.out.println("The door creaks open, revealing a secret passage!");
                    // Could unlock a new exit here
                } else {
                    System.out.println("There's nothing to use the key on here.");
                }
                break;
                
            case "bottle":
                System.out.println("You drink from the bottle labeled 'DRINK ME'.");
                System.out.println("You feel yourself shrinking... but then it wears off.");
                break;
                
            case "cake":
                System.out.println("You eat a piece of the 'EAT ME' cake.");
                System.out.println("You grow taller momentarily, then return to normal size.");
                break;
                
            case "mushroom":
                System.out.println("You nibble on the mushroom. Strange colors swirl around you.");
                System.out.println("When your vision clears, everything seems slightly... different.");
                break;
                
            case "key to castle":
                if (currentLocation == 7) { // Queen's Castle
                    System.out.println("You use the heart-shaped key on the castle gate.");
                    System.out.println("The gate swings open! You've reached the Queen's inner sanctum!");
                    gameWon = true;
                } else {
                    System.out.println("This key seems meant for something special. Perhaps a castle?");
                }
                break;
                
            default:
                System.out.println("You examine the " + item.getName() + " but aren't sure how to use it here.");
        }
    }

    /**
     * Talk to a character in the current room.
     */
    private void talkToCharacter(String characterName) {
        Locations location = map[currentLocation];
        Characters character = location.getCharacter(characterName);
        
        if (character == null) {
            System.out.println("There's no one called '" + characterName + "' here.");
            return;
        }
        
        System.out.println("\n" + character.getName() + " says:");
        System.out.println(character.speak());
    }

    /**
     * Search the current location for hidden items.
     */
    private void searchLocation() {
        Locations location = map[currentLocation];
        
        if (location.hasBeenSearched()) {
            System.out.println("You've already searched this area thoroughly.");
            return;
        }
        
        location.setSearched(true);
        boolean foundSomething = false;
        
        for (Items item : location.getItems()) {
            if (item.isHidden()) {
                item.setHidden(false);
                System.out.println("You found a hidden item: " + item.getName() + "!");
                foundSomething = true;
            }
        }
        
        if (!foundSomething) {
            System.out.println("You search the area but don't find anything hidden.");
        }
    }

    /**
     * Stash an item in the safe room.
     */
    private void stashItem(String itemName) {
        Locations location = map[currentLocation];
        
        if (!location.isSafeRoom()) {
            System.out.println("You can only stash items in a safe room (Tea Party).");
            return;
        }
        
        Items item = inventory.getItemByName(itemName);
        if (item == null) {
            System.out.println("You're not carrying a '" + itemName + "'.");
            return;
        }
        
        inventory.removeItem(item);
        location.stashItem(item);
        System.out.println("You safely stash the " + item.getName() + " at the Tea Party.");
    }

    /**
     * Retrieve an item from the safe room stash.
     */
    private void unstashItem(String itemName) {
        Locations location = map[currentLocation];
        
        if (!location.isSafeRoom()) {
            System.out.println("You can only unstash items from a safe room (Tea Party).");
            return;
        }
        
        Items item = location.takeFromStash(itemName);
        if (item == null) {
            System.out.println("There's no '" + itemName + "' in the stash.");
            listStash();
            return;
        }
        
        inventory.addItem(item);
        System.out.println("You retrieve the " + item.getName() + " from the stash.");
    }

    /**
     * List items in the current stash.
     */
    private void listStash() {
        Locations location = map[currentLocation];
        if (!location.isSafeRoom()) return;
        
        List<Items> stash = location.getStash();
        if (stash.isEmpty()) {
            System.out.println("The stash is empty.");
        } else {
            System.out.println("Stashed items: ");
            for (Items item : stash) {
                System.out.println("  - " + item.getName());
            }
        }
    }

    /**
     * Display a random hint.
     */
    private void displayHint() {
        System.out.println("\n[HINT] " + hints[random.nextInt(hints.length)]);
    }

    /**
     * Display help information.
     */
    private void displayHelp() {
        control.printHelp();
    }

    // ============ Game Events ============

    /**
     * Check for random encounters based on inventory size.
     */
    private void checkRandomEncounter() {
        if (inventory.size() >= ENCOUNTER_THRESHOLD && 
            random.nextInt(ENCOUNTER_CHANCE_DENOMINATOR) < ENCOUNTER_CHANCE_NUMERATOR) {
            Locations location = map[currentLocation];
            
            // Skip encounters in safe room
            if (location.isSafeRoom()) return;
            
            String[] encounters = {
                "A deck of card soldiers marches by, eyeing your belongings suspiciously!",
                "The Cheshire Cat's grin appears. 'Carrying quite a lot, aren't we?' he purrs.",
                "You hear the Queen shouting in the distance: 'WHO'S STEALING MY THINGS?!'",
                "A nervous White Rabbit rushes past, almost bumping into you."
            };
            
            System.out.println("\n*** " + encounters[random.nextInt(encounters.length)] + " ***\n");
        }
    }

    // ============ Resource Loading ============

    /**
     * Load all game resources from text files.
     */
    private void loadGameWorld() {
        loadLocations();
        loadItems();
        loadCharacters();
        
        // Set Tea Party as safe room
        if (map != null && map.length > TEA_PARTY_LOCATION) {
            map[TEA_PARTY_LOCATION].setSafeRoom(true);
        }
        
        // Verify requirements
        System.out.println("Game loaded: " + map.length + " locations, " + 
                          allItems.size() + " items, " + allCharacters.size() + " characters.");
    }

    /**
     * Load locations from resource file.
     */
    private void loadLocations() {
        List<String> lines = readResourceFile("Resource/locations.txt");
        if (lines.isEmpty()) {
            System.out.println("Error: Could not load locations.");
            return;
        }
        
        map = new Locations[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            Locations loc = Locations.fromResourceLine(lines.get(i), i);
            if (loc != null) {
                map[i] = loc;
            }
        }
    }

    /**
     * Load items from resource file and place them in locations.
     */
    private void loadItems() {
        List<String> lines = readResourceFile("Resource/items.txt");
        
        for (String line : lines) {
            Items item = Items.fromResourceLine(line);
            if (item != null) {
                allItems.add(item);
                int locId = item.getDefaultLocationId();
                if (locId >= 0 && locId < map.length && map[locId] != null) {
                    map[locId].addItem(item);
                }
            }
        }
    }

    /**
     * Load characters from resource file and place them in locations.
     */
    private void loadCharacters() {
        List<String> lines = readResourceFile("Resource/characters.txt");
        
        for (String line : lines) {
            Characters character = Characters.fromResourceLine(line);
            if (character != null) {
                allCharacters.add(character);
                int locId = character.getDefaultLocationId();
                if (locId >= 0 && locId < map.length && map[locId] != null) {
                    map[locId].addCharacter(character);
                }
            }
        }
    }

    /**
     * Read lines from a resource file.
     */
    private List<String> readResourceFile(String filename) {
        List<String> lines = new ArrayList<>();
        
        try {
            // Try loading as a resource from classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                // Fallback: try relative to source directory
                is = getClass().getResourceAsStream("/" + filename);
            }
            if (is == null) {
                // Fallback: try as file path using configurable base path
                java.io.File file = new java.io.File(RESOURCE_BASE_PATH + filename);
                if (file.exists()) {
                    is = new java.io.FileInputStream(file);
                }
            }
            
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
                reader.close();
            } else {
                System.out.println("Warning: Could not find resource: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error reading " + filename + ": " + e.getMessage());
        }
        
        return lines;
    }

    // ============ Game Messages ============

    /**
     * Print welcome message.
     */
    private void printWelcome() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     WELCOME TO WONDERLAND                ║");
        System.out.println("║     A Text Adventure Game                ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ Your goal: Find the key to the Queen's   ║");
        System.out.println("║ Castle and use it to enter the palace.   ║");
        System.out.println("║                                          ║");
        System.out.println("║ Explore Wonderland, collect items,       ║");
        System.out.println("║ talk to characters, and discover         ║");
        System.out.println("║ secrets along the way.                   ║");
        System.out.println("║                                          ║");
        System.out.println("║ The Tea Party is a safe place to         ║");
        System.out.println("║ stash items you want to keep.            ║");
        System.out.println("║                                          ║");
        System.out.println("║ Type 'help' for available commands.      ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
    }

    /**
     * Print victory message.
     */
    private void printVictory() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         CONGRATULATIONS!                 ║");
        System.out.println("║                                          ║");
        System.out.println("║    You've entered the Queen's Castle     ║");
        System.out.println("║    and completed your adventure in       ║");
        System.out.println("║    Wonderland!                           ║");
        System.out.println("║                                          ║");
        System.out.println("║    Thank you for playing!                ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
    }

    // ============ TODO: Save/Load ============
    
    // TODO: Implement save game functionality
    // - Save current location, inventory, visited rooms, stashed items
    // - Use serialization or text file format
    // public void saveGame(String filename) {
    //     // Implementation pending
    // }
    
    // TODO: Implement load game functionality
    // - Restore game state from saved file
    // - Validate saved data before loading
    // public void loadGame(String filename) {
    //     // Implementation pending
    // }
}
