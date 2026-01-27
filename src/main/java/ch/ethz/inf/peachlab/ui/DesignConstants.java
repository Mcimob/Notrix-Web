package ch.ethz.inf.peachlab.ui;

public final class DesignConstants {

    private DesignConstants() {}

    public static final String STYLE_FW_500 = "fw-500";
    public static final String STYLE_FW_700 = "fw-700";

    public static final String STYLE_BACKGROUND_BG = "background-bg";
    public static final String STYLE_BACKGROUND_WHITE = "background-white";

    public static final String STYLE_WIDTH_FULL = "width-full";
    public static final String STYLE_WIDTH_200 = "width-200";
    public static final String STYLE_HEIGHT_FULL = "height-full";
    public static final String STYLE_HEIGHT_150PX = "height-150px";

    public static final String STYLE_MAX_HEIGHT_FULL = "max-height-full";
    public static final String STYLE_MIN_HEIGHT_0 = "min-height-0";

    public static final String STYLE_OVERFLOW_SCROLL = "overflow-scroll";

    public static final String STYLE_POSITION_RELATIVE = "position-relative";
    public static final String STYLE_POSITION_ABSOLUTE = "position-absolute";

    public static final String STYLE_FLEX_COLUMN = "flex-column";
    public static final String STYLE_FLEX_ROW = "flex-row";
    public static final String STYLE_FLEX_CENTER = "flex-center";
    public static final String STYLE_FLEX_BETWEEN = "flex-between";
    public static final String STYLE_FLEX_ALIGN_START = "flex-align-start";
    public static final String STYLE_FLEX_ALIGN_END = "flex-align-end";
    public static final String STYLE_FLEX_JUSTIFY_CENTER = "flex-justify-center";
    public static final String STYLE_FLEX_ALIGN_CENTER = "flex-align-center";
    public static final String STYLE_FLEX_JUSTIFY_END = "flex-justify-end";

    public static final String STYLE_PADDING_S = "padding-s";
    public static final String STYLE_PADDING_M = "padding-m";
    public static final String STYLE_PADDING_L = "padding-l";

    public static final String STYLE_MARGIN_S = "margin-s";
    public static final String STYLE_MARGIN_M = "margin-m";
    public static final String STYLE_MARGIN_L = "margin-l";

    public static final String STYLE_GAP_S = "gap-s";
    public static final String STYLE_GAP_M = "gap-m";
    public static final String STYLE_GAP_L = "gap-l";

    public static final String STYLE_BORDER_RADIUS_S = "border-radius-s";
    public static final String STYLE_BORDER_RADIUS_M = "border-radius-m";
    public static final String STYLE_BORDER_RADIUS_L = "border-radius-l";

    public static final String STYLE_BORDER_WIDTH_S = "border-width-s";

    public static final String STYLE_BORDER_STYLE_SOLID = "border-style-solid";
    public static final String STYLE_BORDER_STYLE_DASHED = "border-style-dashed";

    public static final String STYLE_BORDER_COLOR_GRAY = "border-color-gray";

    public static final String STYLE_FONT_SIZE_XS = "font-size-xs";
    public static final String STYLE_FONT_SIZE_S = "font-size-s";
    //                         STYLE_FONT_SIZE_M is not needed, as it would just be regular font size
    public static final String STYLE_FONT_SIZE_L = "font-size-l";

    public static final String STYLE_BOX_SHADOW = "box-shadow";

    public static final String STYLE_TEXT_WRAP_NO = "text-wrap-no";

    public static final String STYLE_TEXT_COLOR_WHITE = "text-color-white";
    public static final String STYLE_TEXT_COLOR_GRAY = "text-color-gray";
    public static final String STYLE_TEXT_LINK = "text-link";

    public static final String STYLE_CELL = "cell";

    public static class StageColors {
        public static final String[] COLORS = {
                "#D3B484", // 0: Environment (浅绿色)
                "#9EB9F3", // 1: Data_Extraction (浅蓝色/青色)
                "#8BE0A4", // 2: Data_Transform (黄绿色)
                "#66C5CC", // 3: EDA (浅蓝色)
                "#C9DB74", // 4: Visualization (薄荷绿色)
                "#87C55F", // 5: Feature_Engineering (浅薰衣草色/紫色)
                "#F6CF71", // 6: Hyperparam_Tuning (浅橙色/桃色)
                "#F89C74", // 7: Model_Train (粉色)
                "#FE88B1", // 8: Model_Evaluation (浅紫色)
                "#DCB0F2", // 9: Data_Export (浅棕色/米色)
                "#B3B3B3", // 10: Commented (浅橙色)
                "#B3B3B3", // 11: Debug (灰色) - 不用于着色
                "#B3B3B3"  // 12: Other (灰色)
        };
    }
}
