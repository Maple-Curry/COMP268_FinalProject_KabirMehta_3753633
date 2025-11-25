public class Characters {
    private String name;
    private String description;
    private int locationId;

    public Characters(String name, String description) {
        this(name, description, -1);
    }

    public Characters(String name, String description, int locationId) {
        this.name = name;
        this.description = description;
        this.locationId = locationId;
    }

    /**
     * Parse a character from a resource file line.
     * Format: name|description|locationId
     */
    public static Characters fromResourceLine(String line) {
        String[] parts = line.split("\\|");
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

    public int getLocationId() {
        return locationId;
    }

    /**
     * Have the character speak a line of dialogue.
     */
    public void speak(String dialogue) {
        System.out.println(name + " says: \"" + dialogue + "\"");
    }

    /**
     * Have the character speak their description.
     */
    public void speak() {
        speak(description);
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
