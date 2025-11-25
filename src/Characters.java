import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Characters.java - Represents a character in the Wonderland text adventure.
 * Characters have names, descriptions, and can speak dialogue.
 */
public class Characters {
    private String name;
    private String description;
    private int defaultLocationId;
    private List<String> dialogues = new ArrayList<>();
    private static Random random = new Random();

    /**
     * Full constructor with location.
     */
    public Characters(String name, String description, int locationId) {
        this.name = name;
        this.description = description;
        this.defaultLocationId = locationId;
    }

    /**
     * Simple constructor without location.
     */
    public Characters(String name, String description) {
        this(name, description, -1);
    }

    /**
     * Parse a character from a resource file line.
     * Format: name|description|locationId
     * @param line The line from the resource file
     * @return A new Characters object, or null if parsing fails
     */
    public static Characters fromResourceLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            System.out.println("Warning: Invalid character line: " + line);
            return null;
        }
        try {
            String name = parts[0].trim();
            String description = parts[1].trim();
            int locationId = Integer.parseInt(parts[2].trim());
            return new Characters(name, description, locationId);
        } catch (NumberFormatException e) {
            System.out.println("Warning: Could not parse character location: " + line);
            return null;
        }
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

    /**
     * Add a dialogue line for this character.
     */
    public void addDialogue(String dialogue) {
        dialogues.add(dialogue);
    }

    /**
     * Get a random dialogue from this character, or default to description.
     */
    public String speak() {
        if (dialogues.isEmpty()) {
            return "\"" + description + "\"";
        }
        return "\"" + dialogues.get(random.nextInt(dialogues.size())) + "\"";
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Characters other = (Characters) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
