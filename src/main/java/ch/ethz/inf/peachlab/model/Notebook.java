package ch.ethz.inf.peachlab.model;

import ch.ethz.inf.peachlab.model.entity.CellEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;

import java.util.List;

public record Notebook(
    List<Cell> cells
) {
    public static record Cell(
        String cell_type,
        String source
    ) {
        public static Cell ofCell(CellEntity cell) {
            return new Cell(cell.getCellType().getJsonName(), cell.getSource());
        }
    }

    public record Metadata() {}

    public static Notebook ofKernel(KernelEntity kernel) {
        return new Notebook(kernel.getCells().stream().map(Cell::ofCell).toList());
    }
}
