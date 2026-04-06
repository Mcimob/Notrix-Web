import {CellData, KernelData, LabelData} from "Frontend/src/react/matrix/notebook-matrix";
import React from "react";

type CellProps = {
    labelNr: number;
    numLines: number;
    getLabel: (id: number) => LabelData;
} & React.ComponentPropsWithoutRef<'div'>;

function Cell({labelNr, getLabel, numLines, ...props} : CellProps) {
    const cellHeight = (numLines: number) =>
        Math.max(3, Math.min(20, 3 + numLines * 0.8));

    return <div
        style={{
            backgroundColor: `var(--clr-stage-${labelNr}, white)`,
            height: `var(--cell-height, ${cellHeight(numLines)}px)`,
            flexShrink: 0,
            display: labelNr == -1 ? "var(--display-md)" : "block",
            border: labelNr == -1 ? "1px solid #bbb" : `1px solid var(--clr-stage-${labelNr})`,
        }}
        {...props}
    />
}

type CellColumnProps = {
    kernel: KernelData;
    getLabel: (id: number) => LabelData;
    getTooltip: (kernel: KernelData, label: number, numLines: number) => string;
    clickListener?: (cellIndex: number) => void;
    style: React.CSSProperties;
} & React.ComponentPropsWithoutRef<'div'>;

export default function CellColumn({kernel, getLabel, getTooltip, clickListener, style, ...props}: CellColumnProps) {
    props.onMouseOver
    const result = [];
    const labelSequenceWithMd = kernel.labelSequenceWithMd;
    const labelSequence = labelSequenceWithMd.filter(i => i >= 0);

    let sequenceIndex = 0;
    for (let i = 0; i < labelSequenceWithMd.length; i++) {
        const label = labelSequenceWithMd[i];
        let className = `width-full cell stage-${label}`;
        if (label != -1 && labelSequence.length != 0) {
            if (sequenceIndex + 1 < labelSequence.length) {
                className = className.concat(` transition-${labelSequence[sequenceIndex]}-${labelSequence[sequenceIndex + 1]}`);
            }
            if (sequenceIndex - 1 >= 0) {
                className = className.concat(` transition-${labelSequence[sequenceIndex - 1]}-${labelSequence[sequenceIndex]}`);
            }
            sequenceIndex++;
        }
        const numLines = kernel.lengthSequence[i];

        result.push(<Cell
            labelNr={label}
            numLines={numLines}
            getLabel={getLabel}
            className={className}
            data-tooltip={getTooltip(kernel, label, numLines)}
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
            height: "fit-content",
            display: "inline-flex"}}
        className={"flex-column"}
        {...props}>
        {result}
    </div>;
}