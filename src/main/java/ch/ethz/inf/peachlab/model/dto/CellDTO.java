package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.CellEntity;
import ch.ethz.inf.peachlab.model.enums.CellType;
import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.Optional;

public record CellDTO(
        int sourceLinesCount,
        int cellType,
        int mainLabel) {

    public static CellDTO ofCell(CellEntity cell) {
        return new CellDTO(
                cell.getSourceLinesCount(),
        Optional.ofNullable(cell.getCellType()).map(CellType::ordinal).orElse(-1),
        Optional.ofNullable(cell.getMainLabel()).map(MainLabel::ordinal).orElse(-1));
    }
}
