public enum Actions {
    GO,
    LOOK,
    TAKE,
    DROP,
    INVENTORY,
    USE,
    TALK,
    STASH,
    UNSTASH,
    SAVE,
    LOAD,
    HELP,
    QUIT;

    public static Actions fromString(String input) {
        switch (input.toLowerCase()) {
            case "go": case "north": case "south": case "east": case "west":
            case "n": case "s": case "e": case "w":
                return GO;
            case "look": case "l": case "examine":
                return LOOK;
            case "take": case "get": case "pick":
                return TAKE;
            case "drop": case "put":
                return DROP;
            case "inventory": case "i": case "inv":
                return INVENTORY;
            case "use":
                return USE;
            case "talk": case "speak":
                return TALK;
            case "stash": case "store":
                return STASH;
            case "unstash": case "retrieve":
                return UNSTASH;
            case "save":
                return SAVE;
            case "load":
                return LOAD;
            case "help": case "h": case "?":
                return HELP;
            case "quit": case "exit": case "q":
                return QUIT;
            default:
                return null;
        }
    }
}
