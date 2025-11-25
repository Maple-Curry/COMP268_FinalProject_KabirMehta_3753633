public class Characters {
    private String name;
    private String description;
    private int defaultLocationId;

    public Characters(String name, String description) {
        this(name, description, -1);
    }

    public Characters(String name, String description, int defaultLocationId) {
        this.name = name;
        this.description = description;
        this.defaultLocationId = defaultLocationId;
    }

    // Parse a character from a resource line (format: name|description|locationId)
    public static Characters fromResourceLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            return null;
        }
        String name = parts[0].trim();
        String description = parts[1].trim();
        int locationId = Integer.parseInt(parts[2].trim());
        return new Characters(name, description, locationId);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDefaultLocationId() {
        return defaultLocationId;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}

