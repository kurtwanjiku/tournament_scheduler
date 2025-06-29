package tournament.scheduler.masterdata;

public class League {
    private int id;
    private String countryCode;
    private String name;
    private int hierarchyLevel;

    public League(int id, String countryCode, String name, int hierarchyLevel) {
        this.id = id;
        this.countryCode = countryCode;
        this.name = name;
        this.hierarchyLevel = hierarchyLevel;
    }

    public League(String countryCode, String name, int hierarchyLevel) {
        this(-1, countryCode, name, hierarchyLevel);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }
} 