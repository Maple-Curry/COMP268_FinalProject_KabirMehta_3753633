public class Characters {
    private final String name;
    private final String description;
    private final int defaultLocationId;

    public Characters(String name, String description, int defaultLocationId) {
        this.name = name == null ? "Unknown Character" : name.trim();
        this.description = description == null ? "No description provided." : description.trim();
        this.defaultLocationId = defaultLocationId;
    }

    // Legacy constructor for compatibility
    public Characters(String name, String description) {
        this(name, description, -1);
    }

    public static Characters fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            System.out.println("Invalid character line: " + line);
            return null;
        }
        String charName = parts[0].trim();
        String charDescription = parts[1].trim();
        int locationId = Integer.parseInt(parts[2].trim());
        return new Characters(charName, charDescription, locationId);
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

    public void speak() {
        System.out.println(name + " says: \"" + description + "\"");
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
