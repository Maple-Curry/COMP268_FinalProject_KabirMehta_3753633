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
    private static final int TEA_PARTY_LOCATION = 3; // Tea Party is the safe room

    // Main game loop
    public void start() {
        loadGameWorld();
        System.out.println("Welcome to Wonderland! Your goal is to explore and survive.");
        System.out.println("Type 'help' for a list of commands.\n");

        boolean playing = true;

        while (playing) {
            Locations location = map[currentLocation];
            System.out.println("\n--- " + location.getName() + " ---");
            System.out.println(location.getDescription(!location.hasBeenVisited()));

            // Check for inventory-triggered encounters
            checkInventoryEncounters(location);

            // Get player input
            String command = control.getCommand();
            if (command.isEmpty()) {
                continue;
            }

            String[] parts = command.split(" ", 2);
            Actions action = Actions.fromString(parts[0]);

            if (action == null) {
                System.out.println("I don't understand that command. Type 'help' for options.");
                continue;
            }

            switch (action) {
                case GO:
                    handleGo(location, parts);
                    break;

                case LOOK:
                    handleLook(location);
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
                        talkToCharacter(location, parts[1]);
                    } else {
                        System.out.println("Talk to whom?");
                    }
                    break;

                case STASH:
                    if (parts.length > 1) {
                        stashItem(location, parts[1]);
                    } else {
                        System.out.println("Stash what?");
                    }
                    break;

                case UNSTASH:
                    if (parts.length > 1) {
                        unstashItem(location, parts[1]);
                    } else {
                        listStash(location);
                    }
                    break;

                case SAVE:
                    saveGame();
                    break;

                case LOAD:
                    loadSavedGame();
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

    // Handle GO command with direction parsing
    private void handleGo(Locations location, String[] parts) {
        if (parts.length < 2) {
            System.out.println("Go where? (north, south, east, west)");
            return;
        }
        String direction = parts[1];
        int nextRoom = location.getExit(direction);
        if (nextRoom != -1 && nextRoom < map.length) {
            currentLocation = nextRoom;
        } else {
            System.out.println("You can't go that way.");
        }
    }

    // Handle LOOK command - show items and characters
    private void handleLook(Locations location) {
        ArrayList<Items> visibleItems = location.getVisibleItems();
        ArrayList<Characters> characters = location.getCharacters();
        
        if (visibleItems.isEmpty() && characters.isEmpty()) {
            System.out.println("You don't see anything of interest.");
            return;
        }
        
        System.out.println("You see:");
        for (Items item : visibleItems) {
            System.out.println("- " + item.getName() + ": " + item.getDescription());
        }
        for (Characters character : characters) {
            System.out.println("- " + character.getName() + ": " + character.getDescription());
        }
        
        // Show stash info if in safe room
        if (location.isSafeRoom() && !location.getStash().isEmpty()) {
            System.out.println("\nYour stash contains:");
            for (Items item : location.getStash()) {
                System.out.println("- " + item.getName());
            }
        }
    }

    // Show player inventory
    public void showInventory() {
        inventory.listItems();
    }

    // Handle taking an item from the room
    private void takeItem(Locations currentRoom, String itemName) {
        Items item = currentRoom.getItem(itemName);
        if (item != null && item.isCollectible()) {
            if (item.isHidden()) {
                System.out.println("That item is not here.");
                return;
            }
            inventory.addItem(item);
            currentRoom.removeItem(item);
            System.out.println("You picked up: " + item.getName());
        } else if (item != null && !item.isCollectible()) {
            System.out.println("You can't take that.");
        } else {
            System.out.println("That item is not here.");
        }
    }

    // Handle dropping an item into the room
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
        if (item == null) {
            System.out.println("You don't have that item.");
            return;
        }

        // Special item effects based on current location and item
        switch (itemName.toLowerCase()) {
            case "bottle":
                System.out.println("You drink from the bottle. The world seems to grow larger around you...");
                break;
            case "cake":
                System.out.println("You eat the cake. The world seems to shrink around you...");
                break;
            case "small key":
                if (currentLocation == 1) { // Hall of Doors
                    System.out.println("You unlock a tiny door revealing a beautiful garden beyond!");
                } else {
                    System.out.println("There's nothing to unlock here.");
                }
                break;
            case "mushroom":
                System.out.println("You nibble the mushroom. Strange visions dance before your eyes...");
                break;
            default:
                System.out.println("You can't figure out how to use the " + item.getName() + " right now.");
        }
    }

    // Handle talking to a character
    private void talkToCharacter(Locations location, String characterName) {
        Characters character = location.getCharacter(characterName);
        if (character == null) {
            System.out.println("There's no one by that name here.");
            return;
        }

        // Character dialogue based on who you talk to
        switch (character.getName().toLowerCase()) {
            case "white rabbit":
                System.out.println("\"Oh dear! Oh dear! I shall be too late!\" The Rabbit hurries off.");
                break;
            case "doorknob":
                System.out.println("\"Ouch! Do you mind? I'm trying to sleep here. If you want through, find the key.\"");
                break;
            case "caterpillar":
                System.out.println("\"Who... are... you?\" The Caterpillar blows a smoke ring and waits.");
                break;
            case "mad hatter":
                System.out.println("\"Why is a raven like a writing desk? Have some tea!\"");
                break;
            case "march hare":
                System.out.println("\"No room! No room!\" screams the March Hare, despite the empty seats.");
                break;
            case "cheshire cat":
                System.out.println("\"We're all mad here. I'm mad. You're mad.\" The cat grins wider.");
                break;
            case "queen of hearts":
                System.out.println("\"OFF WITH THEIR HEAD!\" The Queen points at you menacingly.");
                break;
            case "king of hearts":
                System.out.println("\"Hmm? What? Oh, yes, whatever she said.\" The King continues reading.");
                break;
            default:
                System.out.println(character.getName() + " doesn't seem interested in talking right now.");
        }
    }

    // Stash an item in the safe room
    private void stashItem(Locations location, String itemName) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only stash items at the Tea Party safe room.");
            return;
        }

        Items item = inventory.getItemByName(itemName);
        if (item == null) {
            System.out.println("You don't have that item.");
            return;
        }

        location.stashItem(item);
        inventory.removeItem(item);
        System.out.println("You stashed the " + item.getName() + " safely at the Tea Party.");
    }

    // Retrieve an item from the stash
    private void unstashItem(Locations location, String itemName) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only access your stash at the Tea Party safe room.");
            return;
        }

        Items item = location.takeFromStash(itemName);
        if (item == null) {
            System.out.println("That item is not in your stash.");
            return;
        }

        inventory.addItem(item);
        System.out.println("You retrieved the " + item.getName() + " from your stash.");
    }

    // List items in the stash
    private void listStash(Locations location) {
        if (!location.isSafeRoom()) {
            System.out.println("You can only access your stash at the Tea Party safe room.");
            return;
        }

        ArrayList<Items> stash = location.getStash();
        if (stash.isEmpty()) {
            System.out.println("Your stash is empty.");
        } else {
            System.out.println("Items in your stash:");
            for (Items item : stash) {
                System.out.println("- " + item.getName());
            }
        }
    }

    // Check for inventory-triggered encounters
    private void checkInventoryEncounters(Locations location) {
        // Queen encounter if carrying jam tart
        if (currentLocation == 4 && inventory.hasItem("jam tart")) {
            System.out.println("\nThe Queen of Hearts spots the jam tart in your possession!");
            System.out.println("\"THAT'S MY TART! OFF WITH THEIR HEAD!\" she screams.");
            System.out.println("You'd better drop it or use it wisely...");
        }

        // Cheshire Cat hints based on inventory
        if (currentLocation == 6 && !inventory.isEmpty()) {
            System.out.println("\nThe Cheshire Cat grins at your belongings.");
            System.out.println("\"Interesting treasures you carry. Some may help, some may hinder...\"");
        }
    }

    // TODO: Save game functionality
    private void saveGame() {
        // TODO: Implement save functionality
        // Should save: currentLocation, inventory items, visited locations, stash contents
        System.out.println("Save functionality coming soon!");
    }

    // TODO: Load game functionality
    private void loadSavedGame() {
        // TODO: Implement load functionality
        // Should restore: currentLocation, inventory items, visited locations, stash contents
        System.out.println("Load functionality coming soon!");
    }

    // Load game world from resource files
    private void loadGameWorld() {
        loadLocations();
        loadItems();
        loadCharacters();

        // Set Tea Party as safe room
        if (map != null && TEA_PARTY_LOCATION < map.length) {
            map[TEA_PARTY_LOCATION].setSafeRoom(true);
        }

        System.out.println("Game world loaded successfully!");
    }

    // Load locations from resource file
    private void loadLocations() {
        List<String> lines = readResourceFile("Resource/locations.txt");
        if (lines.isEmpty()) {
            System.out.println("Warning: Could not load locations. Using defaults.");
            createDefaultLocations();
            return;
        }

        map = new Locations[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            Locations loc = Locations.fromResourceLine(lines.get(i));
            if (loc != null) {
                map[i] = loc;
            }
        }
    }

    // Load items from resource file and place in locations
    private void loadItems() {
        List<String> lines = readResourceFile("Resource/items.txt");
        if (lines.isEmpty()) {
            System.out.println("Warning: Could not load items.");
            return;
        }

        for (String line : lines) {
            Items item = Items.fromResourceLine(line);
            if (item != null && item.getDefaultLocationId() >= 0 && item.getDefaultLocationId() < map.length) {
                map[item.getDefaultLocationId()].addItem(item);
            }
        }
    }

    // Load characters from resource file and place in locations
    private void loadCharacters() {
        List<String> lines = readResourceFile("Resource/characters.txt");
        if (lines.isEmpty()) {
            System.out.println("Warning: Could not load characters.");
            return;
        }

        for (String line : lines) {
            Characters character = Characters.fromResourceLine(line);
            if (character != null && character.getDefaultLocationId() >= 0 && character.getDefaultLocationId() < map.length) {
                map[character.getDefaultLocationId()].addCharacter(character);
            }
        }
    }

    // Read lines from a resource file (using classloader)
    private List<String> readResourceFile(String resourcePath) {
        List<String> lines = new ArrayList<>();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                // Try loading from file system as fallback
                try {
                    java.nio.file.Path path = java.nio.file.Paths.get("src/" + resourcePath);
                    return java.nio.file.Files.readAllLines(path);
                } catch (IOException e2) {
                    return lines;
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            // Silent fallback - will return empty list
        }
        return lines;
    }

    // Create default locations if resource loading fails
    private void createDefaultLocations() {
        map = new Locations[3];
        map[0] = new Locations("Rabbit Hole", "A spiraling tunnel. You fell a long way to get here.", new int[]{1, -1, -1, -1});
        map[1] = new Locations("Hall of Doors", "A hallway with many doors of various sizes.", new int[]{-1, 0, 2, -1});
        map[2] = new Locations("Garden", "A beautiful garden with talking flowers.", new int[]{-1, 1, -1, -1});
    }

    // Get current location index (for testing/debugging)
    public int getCurrentLocation() {
        return currentLocation;
    }

    // Get inventory reference (for testing/debugging)
    public Inventory getInventory() {
        return inventory;
    }
}
