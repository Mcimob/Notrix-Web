package ch.ethz.inf.peachlab.model;

import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;

import java.util.List;

public record Notebook(
    List<Cell> cells
) {
    public record Cell(
        String cell_type,
        String source
    ) {
        public static Cell ofCell(HasCellData cell) {
            return new Cell(cell.getCellType().getJsonName(), cell.getSource());
        }
    }

    public record Metadata() {}

    public static Notebook ofKernel(HasKernelData<?, ?> kernel) {
        return new Notebook(kernel.getCells().stream().map(Cell::ofCell).toList());
    }
}
