package ch.ethz.inf.peachlab.model.enums;

public enum CellType {
    CODE("code"),
    MARKDOWN("markdown"),
    RAW("raw"),
    HEADING("heading");

    private final String jsonName;

    CellType(String jsonName) {
        this.jsonName = jsonName;
    }

    public String getJsonName() {
        return jsonName;
    }
}
