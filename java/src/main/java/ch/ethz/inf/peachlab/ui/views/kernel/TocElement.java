package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.model.entity.HasCellData;

public record TocElement(int level, String title, HasCellData cell) {
}
