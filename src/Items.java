public class Items {
    private String name;
    private String description;
    private boolean collectible;
    private int defaultLocationId;
    private boolean hidden;

    // Original constructor for backward compatibility
    public Items(String name, String description, boolean collectible) {
        this(name, description, collectible, -1, false);
    }

    // New constructor with hidden flag and default location
    public Items(String name, String description, boolean collectible, int defaultLocationId, boolean hidden) {
        this.name = name;
        this.description = description;
        this.collectible = collectible;
        this.defaultLocationId = defaultLocationId;
        this.hidden = hidden;
    }

    // Parse an item from a resource line (format: name|description|collectible|locationId)
    public static Items fromResourceLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            return null;
        }
        String name = parts[0].trim();
        String description = parts[1].trim();
        boolean collectible = Boolean.parseBoolean(parts[2].trim());
        int locationId = Integer.parseInt(parts[3].trim());
        boolean hidden = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());
        return new Items(name, description, collectible, locationId, hidden);
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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
