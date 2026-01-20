package ch.ethz.inf.peachlab.model.enums;

public enum MainLabel {
    ENVIRONMENT("#D3B484", "entity.cell.mainLabel.0"), // (浅绿色)
    DATA_EXTRACTION("#9EB9F3", "entity.cell.mainLabel.1"), // (浅蓝色/青色)
    DATA_TRANSFORM("#8BE0A4", "entity.cell.mainLabel.2"), // (黄绿色)
    EDA("#66C5CC", "entity.cell.mainLabel.3"), // (浅蓝色)
    VISUALIZATION("#C9DB74", "entity.cell.mainLabel.4"), // (薄荷绿色)
    FEATURE_ENGINEERING("#87C55F", "entity.cell.mainLabel.5"), // (浅薰衣草色/紫色)
    HYPERPARAM_TUNING("#F6CF71", "entity.cell.mainLabel.6"), // (浅橙色/桃色)
    MODEL_TRAIN("#F89C74", "entity.cell.mainLabel.7"), // (粉色)
    MODEL_EVALUATION("#FE88B1", "entity.cell.mainLabel.8"), // (浅紫色)
    DATA_EXPORT("#DCB0F2", "entity.cell.mainLabel.9"), // (浅棕色/米色)
    COMMENTED("#B3B3B3", "entity.cell.mainLabel.10"), // (浅橙色)
    DEBUG("#B3B3B3", "entity.cell.mainLabel.11"), // (灰色) - 不用于着色
    OTHER("#B3B3B3", "entity.cell.mainLabel.11"); // (灰色)
    private final String color;
    private final String titleKey;

    MainLabel(String color, String titleKey) {
        this.color = color;
        this.titleKey = titleKey;
    }

    public String getColor() {
        return color;
    }

    public String getTitleKey() {
        return titleKey;
    }
}
