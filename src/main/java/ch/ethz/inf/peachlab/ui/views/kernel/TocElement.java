package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.model.entity.CellEntity;

public record TocElement(int level, String title, CellEntity cell) {
}
