import {CellData, KernelData, LabelData} from "Frontend/src/react/matrix/notebook-matrix";
import React from "react";

type CellProps = {
    cell: CellData;
    getLabel: (id: number) => LabelData;
} & React.ComponentPropsWithoutRef<'div'>;

function Cell({cell, getLabel, ...props} : CellProps) {
    const cellHeight = (item: CellData) =>
        Math.max(3, Math.min(20, 3 + item.sourceLinesCount * 0.8));

    return <div
        style={{
            backgroundColor: `var(--clr-stage-${cell.mainLabel}, white)`,
            height: `var(--cell-height, ${cellHeight(cell)}px)`,
            flexShrink: 0,
            display: cell.mainLabel == -1 ? "var(--display-md)" : "block",
            border: cell.mainLabel == -1 ? "1px solid #bbb" : `1px solid var(--clr-stage-${cell.mainLabel})`,
        }}
        {...props}
    />
}

type CellColumnProps = {
    kernel: KernelData;
    getLabel: (id: number) => LabelData;
    getTooltip: (kernel: KernelData, cell: CellData) => string;
    clickListener?: (cellIndex: number) => void;
    style: React.CSSProperties;
} & React.ComponentPropsWithoutRef<'div'>;

export default function CellColumn({kernel, getLabel, getTooltip, clickListener, style, ...props}: CellColumnProps) {
    const result = [];
    const cells = kernel.cells;
    const labelSequence = kernel.labelSequence;
    let sequenceIndex = 0;
    for (let i = 0; i < cells.length; i++) {
        const cell = cells[i];
        let className = `width-full cell stage-${cell.mainLabel}`;

        if (cell.mainLabel != -1 && labelSequence.length != 0) {
            if (sequenceIndex + 1 < labelSequence.length) {
                className = className.concat(` transition-${labelSequence[sequenceIndex]}-${labelSequence[sequenceIndex + 1]}`);
            }
            if (sequenceIndex - 1 >= 0) {
                className = className.concat(` transition-${labelSequence[sequenceIndex - 1]}-${labelSequence[sequenceIndex]}`);
            }
            sequenceIndex++;
        }

        result.push(<Cell
            cell={cell}
            getLabel={getLabel}
            className={className}
            data-tooltip={getTooltip(kernel, cell)}
            onClick={(e) => {
                clickListener && clickListener(i);
                e.stopPropagation();
            }}
        />)

    }
    return <div
        style={{...style,
            width: "24px",
            gap: "1px",
            margin: "2px",
            padding: "2px",
            outline: (kernel.isUploaded ? "var(--lumo-primary-color-50pct)" : "transparent") + " solid 2px",
            height: "unset"}}
        className={"flex-column"}
        {...props}>
        {result}
    </div>;
}