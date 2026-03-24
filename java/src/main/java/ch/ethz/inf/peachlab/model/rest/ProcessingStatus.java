package ch.ethz.inf.peachlab.model.rest;

import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum ProcessingStatus {
    INIT("Starting analysis", VaadinIcon.FILE_PROCESS),
    EXTRACTING_CELLS("Extracting cells", VaadinIcon.ENVELOPE_OPEN),
    PREDICTING_CELLS("Predicting cells", VaadinIcon.GRID_SMALL),
    STATS("Creating stats", VaadinIcon.BAR_CHART_H),
    CLUSTERING("Clustering", VaadinIcon.CLUSTER),
    GPT("Asking GPT for help", VaadinIcon.COGS),
    DONE("Done", VaadinIcon.CHECK);

    private final String displayText;
    private final IconFactory icon;

    ProcessingStatus(String displayText, IconFactory icon) {
        this.displayText = displayText;
        this.icon = icon;
    }

    public String getDisplayText() {
        return displayText;
    }

    public IconFactory getIcon() {
        return icon;
    }
}
