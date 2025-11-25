/**
 * Items.java - Represents an item in the Wonderland text adventure.
 * Items can be collectible, hidden (requiring search to find), and have a default location.
 */
public class Items {
    private String name;
    private String description;
    private boolean collectible;
    private int defaultLocationId;
    private boolean hidden;

    /**
     * Full constructor with all item properties.
     * @param name Item name
     * @param description Item description
     * @param collectible Whether the item can be picked up
     * @param defaultLocationId The location ID where this item starts
     * @param hidden Whether the item is hidden (requires search to find)
     */
    public Items(String name, String description, boolean collectible, int defaultLocationId, boolean hidden) {
        this.name = name;
        this.description = description;
        this.collectible = collectible;
        this.defaultLocationId = defaultLocationId;
        this.hidden = hidden;
    }

    /**
     * Simple constructor for basic items.
     * @param name Item name
     * @param description Item description
     * @param collectible Whether the item can be picked up
     */
    public Items(String name, String description, boolean collectible) {
        this(name, description, collectible, -1, false);
    }

    /**
     * Parse an item from a resource file line.
     * Format: name|description|collectible|locationId
     * @param line The line from the resource file
     * @return A new Items object, or null if parsing fails
     */
    public static Items fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            System.out.println("Warning: Invalid item line: " + line);
            return null;
        }
        try {
            String name = parts[0].trim();
            String description = parts[1].trim();
            boolean collectible = Boolean.parseBoolean(parts[2].trim());
            int locationId = Integer.parseInt(parts[3].trim());
            boolean hidden = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());
            return new Items(name, description, collectible, locationId, hidden);
        } catch (NumberFormatException e) {
            System.out.println("Warning: Could not parse item location: " + line);
            return null;
        }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Items other = (Items) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
