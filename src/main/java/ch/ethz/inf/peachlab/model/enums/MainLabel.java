package ch.ethz.inf.peachlab.model.enums;

public enum MainLabel {
    ENVIRONMENT("#D3B484", "entity.cell.mainLabel.0", LabelCategory.ENVIRONMENT), // (浅绿色)
    DATA_EXTRACTION("#9EB9F3", "entity.cell.mainLabel.1", LabelCategory.DATA_ORIENTED), // (浅蓝色/青色)
    DATA_TRANSFORM("#8BE0A4", "entity.cell.mainLabel.2", LabelCategory.DATA_ORIENTED), // (黄绿色)
    EDA("#66C5CC", "entity.cell.mainLabel.3", LabelCategory.DATA_ORIENTED), // (浅蓝色)
    VISUALIZATION("#C9DB74", "entity.cell.mainLabel.4", LabelCategory.DATA_ORIENTED), // (薄荷绿色)
    FEATURE_ENGINEERING("#87C55F", "entity.cell.mainLabel.5", LabelCategory.DATA_ORIENTED), // (浅薰衣草色/紫色)
    HYPERPARAM_TUNING("#F6CF71", "entity.cell.mainLabel.6", LabelCategory.MODEL_ORIENTED), // (浅橙色/桃色)
    MODEL_TRAIN("#F89C74", "entity.cell.mainLabel.7", LabelCategory.MODEL_ORIENTED), // (粉色)
    MODEL_EVALUATION("#FE88B1", "entity.cell.mainLabel.8", LabelCategory.MODEL_ORIENTED), // (浅紫色)
    DATA_EXPORT("#DCB0F2", "entity.cell.mainLabel.9", LabelCategory.DATA_EXPORT), // (浅棕色/米色)
    COMMENTED("#B3B3B3", "entity.cell.mainLabel.10", LabelCategory.OTHER), // (浅橙色)
    DEBUG("#B3B3B3", "entity.cell.mainLabel.11", LabelCategory.OTHER), // (灰色) - 不用于着色
    OTHER("#B3B3B3", "entity.cell.mainLabel.11", LabelCategory.OTHER); // (灰色)
    private final String color;
    private final String titleKey;
    private final LabelCategory labelCategory;

    MainLabel(String color, String titleKey, LabelCategory labelCategory) {
        this.color = color;
        this.titleKey = titleKey;
        this.labelCategory = labelCategory;
    }

    public String getColor() {
        return color;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public LabelCategory getLabelCategory() {
        return labelCategory;
    }
}
