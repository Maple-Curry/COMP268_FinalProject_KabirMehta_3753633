import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Game {
    private Locations[] map;
    private int currentLocation = 0;
    private Inventory inventory = new Inventory ();
    private Control control = new Control ();

    // Main game loop
    public void start () {
        loadGameWorld ();  // You’ll replace this with file-based loading
        System.out.println ("Welcome to Wonderland! Your goal is to explore and survive.");

        boolean playing = true;

        while (playing) {
            Locations location = map[currentLocation];
            System.out.println (location.getDescription (! location.hasBeenVisited ()));

            // Get player input
            String command = control.getCommand ();
            String[] parts = command.split (" ", 2);
            Actions action = Actions.fromString (parts[0]);

            if (action == null) {
                System.out.println ("I don't understand that command.");
                continue;
            }

            switch (action) {
                case GO:
                    if (parts.length < 2) {
                        System.out.println ("Go where?");
                    } else {
                        int nextRoom = location.getExit (parts[1]);
                        if (nextRoom != - 1) {
                            currentLocation = nextRoom;
                        } else {
                            System.out.println ("You can't go that way.");
                        }
                    }
                    break;

                case LOOK:
                    System.out.println ("You see:");
                    location.getItems ().forEach (item -> System.out.println ("- " + item.getName ()));
                    location.getCharacters ().forEach (character -> System.out.println ("- " + character.getName ()));
                    break;

                case TAKE:
                    if (parts.length > 1) {
                        takeItem (location, parts[1]);
                    } else {
                        System.out.println ("Take what?");
                    }
                    break;

                case DROP:
                    if (parts.length > 1) {
                        dropItem (location, parts[1]);
                    } else {
                        System.out.println ("Drop what?");
                    }
                    break;

                case INVENTORY:
                    inventory.listItems ();
                    break;

                case HELP:
                    control.printHelp ();
                    break;

                case QUIT:
                    playing = false;
                    System.out.println ("Goodbye!");
                    break;

                default:
                    System.out.println ("I don't understand that command.");
            }
        }
    }

    // Handles item pickup from the current room
    private void takeItem (Locations currentRoom, String itemName) {
        Items item = currentRoom.getItem (itemName);
        if (item != null && item.isCollectible ()) {
            inventory.addItem (item);
            currentRoom.removeItem (item);
            System.out.println ("You picked up: " + item.getName ());
        } else if (item != null && ! item.isCollectible ()) {
            System.out.println ("You can’t take that.");
        } else {
            System.out.println ("That item is not here.");
        }
    }

    // Handles dropping item into the current room
    private void dropItem (Locations currentRoom, String itemName) {
        Items item = inventory.getItemByName (itemName);
        if (item != null) {
            currentRoom.addItem (item);
            inventory.removeItem (item);
            System.out.println ("You dropped: " + item.getName ());
        } else {
            System.out.println ("You don’t have that item.");
        }
    }

    // Placeholder map data — will be replaced with file input
//    private void loadGameWorld() {
//        map = new Locations[3];
//        map[0] = new Locations("Rabbit Hole", "A mysterious hole in the ground.", new int[]{1, -1, -1, -1});
//        map[1] = new Locations("Hallway", "A long hallway with locked doors.", new int[]{-1, 0, 2, -1});
//        map[2] = new Locations("Tea Party", "A mad tea party in full swing.", new int[]{-1, -1, -1, 1});
//    }

    private void loadGameWorld () {
        try {
            // Read all location lines
            List<String> locationLines = Files.readAllLines (Paths.get ("resources/locations.txt"));
            map = new Locations[locationLines.size ()];

            // Parse each line and build Locations
            for (int i = 0; i < locationLines.size (); i++) {
                String line = locationLines.get (i);
                String[] parts = line.split ("\\|");

                String name = parts[0].trim ();
                String description = parts[1].trim ();

                // Parse exits
                String[] exitStrings = parts[2].split (",");
                int[] exits = new int[4];
                for (int j = 0; j < 4; j++) {
                    exits[j] = Integer.parseInt (exitStrings[j].trim ());
                }

                map[i] = new Locations (name, description, exits);
            }

            System.out.println ("Game world loaded with " + map.length + " locations.");

        } catch (IOException e) {
            System.out.println (" Error loading locations.txt: " + e.getMessage ());
            e.printStackTrace ();
        }
    }
}
