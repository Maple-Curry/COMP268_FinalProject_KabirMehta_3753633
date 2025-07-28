public enum Actions {
    GO,
    LOOK,
    TAKE,
    DROP,
    INVENTORY,
    USE,
    HELP,
    QUIT;

    public static Actions fromString(String input) {
        switch (input.toLowerCase()) {
            case "go": case "north": case "south": case "east": case "west": return GO;
            case "look": return LOOK;
            case "take": return TAKE;
            case "drop": return DROP;
            case "inventory": case "i": return INVENTORY;
            case "use": return USE;
            case "help": return HELP;
            case "quit": case "exit": return QUIT;
            default: return null;
        }
    }
}
