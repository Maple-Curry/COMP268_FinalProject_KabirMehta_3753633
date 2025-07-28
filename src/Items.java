public class Items {
    private String name;
    private String description;
    private boolean collectible;

    public Items(String name, String description, boolean collectible) {
        this.name = name;
        this.description = description;
        this.collectible = collectible;
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

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
