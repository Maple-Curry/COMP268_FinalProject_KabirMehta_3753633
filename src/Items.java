import java.util.Objects;

public class Items {
    private String name;
    private String description;
    private boolean collectible;
    private int locationId;
    private boolean hidden;

    public Items(String name, String description, boolean collectible) {
        this(name, description, collectible, -1, false);
    }

    public Items(String name, String description, boolean collectible, int locationId, boolean hidden) {
        this.name = name;
        this.description = description;
        this.collectible = collectible;
        this.locationId = locationId;
        this.hidden = hidden;
    }

    /**
     * Parse an item from a resource file line.
     * Format: name|description|collectible|locationId
     * Optional hidden flag after locationId (default: false)
     */
    public static Items fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Item line cannot be null or empty");
        }
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Item line must have at least 4 fields: " + line);
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

    public int getLocationId() {
        return locationId;
    }

    public boolean startsHidden() {
        return hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void reveal() {
        this.hidden = false;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return Objects.equals(name, items.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
