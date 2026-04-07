package ch.ethz.inf.peachlab.ui.views.competition.components;

import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import com.vaadin.flow.component.grid.Grid;

public class KernelGrid extends Grid<HasKernelData<?, ?, ?>> {

    public KernelGrid() {
        init();
    }

    private void init() {
        setId("kernel-grid");
        setPartNameGenerator(k -> k instanceof UploadedKernelEntity ? "uploaded" : "");

        setSelectionMode(Grid.SelectionMode.SINGLE);

        setEmptyStateText("Loading Notebooks...");
        initColumns();
    }

    private void initColumns() {
        addComponentColumn(TitleLink::ofKernel)
            .setHeader("Title")
            .setSortable(true)
            .setSortProperty("title")
            .setKey("title")
            .setFlexGrow(1);
        addColumn(HasKernelData::getTotalVotes)
            .setHeader("# Votes")
            .setSortable(true)
            .setSortProperty("totalVotes")
            .setKey("totalVotes")
            .setFlexGrow(0);
        addColumn(HasKernelData::getTotalViews)
            .setHeader("# Views")
            .setSortable(true)
            .setSortProperty("totalViews")
            .setKey("totalViews")
            .setFlexGrow(0);
        addColumn(HasKernelData::getCellCount)
            .setHeader("# Cells")
            .setSortable(true)
            .setSortProperty("cellCount")
            .setKey("cellCount")
            .setFlexGrow(0);
        addColumn(HasKernelData::getNumLines)
            .setHeader("# Lines")
            .setSortable(true)
            .setSortProperty("numLines")
            .setKey("numLines")
            .setFlexGrow(0);
    }
}
