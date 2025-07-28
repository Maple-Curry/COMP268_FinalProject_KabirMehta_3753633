import java.util.ArrayList;

public class Game {
    private Locations[] map;
    private int currentLocation = 0;
    private Inventory inventory = new Inventory();
    private Control control = new Control();

    public void start() {
        loadGameWorld();  // You will implement this soon
        System.out.println("Welcome to Wonderland! Your goal is to explore and survive.");

        boolean playing = true;
        while (playing) {
            Locations location = map[currentLocation];
            System.out.println(location.getDescription(!location.visited));

            String command = control.getCommand();
            String[] parts = command.split(" ", 2);
            Actions action = Actions.fromString(parts[0]);

            switch (action) {
                case GO:
                    if (parts.length < 2) {
                        System.out.println("Go where?");
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
                    System.out.println("You see:");
                    location.getItems().forEach(item -> System.out.println("- " + item.getName()));
                    location.getCharacters().forEach(character -> System.out.println("- " + character.getName()));
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
                    inventory.listItems();
                    break;

                case HELP:
                    control.printHelp();
                    break;

                case QUIT:
                    playing = false;
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
            System.out.println("You can’t take that.");
        } else {
            System.out.println("That item is not here.");
        }
    }

    // Handles dropping item into the current room
    private void dropItem(Locations currentRoom, String itemName) {
        Items item = inventory.getItem(itemName);
        if (item != null) {
            currentRoom.addItem(item);
            inventory.removeItem(item);
            System.out.println("You dropped: " + item.getName());
        } else {
            System.out.println("You don’t have that item.");
        }
    }

    private void loadGameWorld() {
        // TEMP: Hardcoded rooms. You’ll replace with file-based version later
        map = new Locations[3];
        map[0] = new Locations("Rabbit Hole", "A mysterious hole in the ground.", new int[]{1, -1, -1, -1});
        map[1] = new Locations("Hallway", "A long hallway with locked doors.", new int[]{-1, 0, 2, -1});
        map[2] = new Locations("Tea Party", "A mad tea party in full swing.", new int[]{-1, -1, -1, 1});
    }
}


