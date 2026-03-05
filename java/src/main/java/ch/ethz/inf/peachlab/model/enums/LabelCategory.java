package ch.ethz.inf.peachlab.model.enums;

public enum LabelCategory {
    DATA_ORIENTED("entity.cell.mainLabel.labelCategory.dataOriented"),
    MODEL_ORIENTED("entity.cell.mainLabel.labelCategory.modelOriented"),
    ENVIRONMENT("entity.cell.mainLabel.labelCategory.environment"),
    DATA_EXPORT("entity.cell.mainLabel.labelCategory.dataExport"),
    OTHER("entity.cell.mainLabel.labelCategory.other");

    private final String titleKey;

    LabelCategory(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getTitleKey() {
        return titleKey;
    }
}
