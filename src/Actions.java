public enum Actions {
    GO,
    LOOK,
    TAKE,
    DROP,
    INVENTORY,
    USE,
    HELP,
    QUIT,
    TALK,
    STASH,
    RETRIEVE,
    SEARCH;

    public static Actions fromString(String input) {
        switch (input.toLowerCase()) {
            case "go": case "north": case "south": case "east": case "west":
            case "n": case "s": case "e": case "w": return GO;
            case "look": case "l": return LOOK;
            case "take": case "get": case "pick": return TAKE;
            case "drop": return DROP;
            case "inventory": case "i": return INVENTORY;
            case "use": return USE;
            case "help": case "h": case "?": return HELP;
            case "quit": case "exit": case "q": return QUIT;
            case "talk": case "speak": return TALK;
            case "stash": case "store": return STASH;
            case "retrieve": case "unstash": return RETRIEVE;
            case "search": case "examine": return SEARCH;
            default: return null;
        }
    }
}
