public enum Actions {
    GO,
    LOOK,
    TAKE,
    DROP,
    INVENTORY,
    USE,
    TALK,
    SEARCH,
    STASH,
    RETRIEVE,
    HINT,
    HELP,
    QUIT;

    public static Actions fromString(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        switch (input.toLowerCase()) {
            case "go": return GO;
            case "look": case "l": case "x": case "examine": return LOOK;
            case "take": case "get": case "pickup": case "pick": case "grab": return TAKE;
            case "drop": return DROP;
            case "inventory": case "i": case "inv": return INVENTORY;
            case "use": return USE;
            case "talk": case "speak": return TALK;
            case "search": return SEARCH;
            case "stash": case "store": return STASH;
            case "retrieve": case "unstash": return RETRIEVE;
            case "hint": return HINT;
            case "help": case "?": return HELP;
            case "quit": case "exit": case "q": return QUIT;
            default: return null;
        }
    }
}
