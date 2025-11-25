public class Items {
    private final String name;
    private final String description;
    private final boolean collectible;
    private final int defaultLocationId;
    private final boolean hidden;

    public Items(String name, String description, boolean collectible, int defaultLocationId, boolean hidden) {
        this.name = name == null ? "Unknown Item" : name.trim();
        this.description = description == null ? "No description provided." : description.trim();
        this.collectible = collectible;
        this.defaultLocationId = defaultLocationId;
        this.hidden = hidden;
    }

    // Legacy constructor for compatibility
    public Items(String name, String description, boolean collectible) {
        this(name, description, collectible, -1, false);
    }

    public static Items fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            System.out.println("Invalid item line: " + line);
            return null;
        }
        String itemName = parts[0].trim();
        String itemDescription = parts[1].trim();
        boolean itemCollectible = Boolean.parseBoolean(parts[2].trim());
        int locationId = Integer.parseInt(parts[3].trim());
        boolean isHidden = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());
        return new Items(itemName, itemDescription, itemCollectible, locationId, isHidden);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCollectible() {
        return collectible;
    }

    public int getDefaultLocationId() {
        return defaultLocationId;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
