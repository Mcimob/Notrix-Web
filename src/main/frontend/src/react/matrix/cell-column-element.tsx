import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import React from "react";
import {DEFAULT_LABEL, KernelData, LabelData} from "Frontend/src/react/matrix/notebook-matrix";
import CellColumn from "Frontend/src/react/matrix/cell-column";


class CellColumnElement extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [kernel, _setKernel] = hooks.useState<KernelData>("kernel");
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])
        const [_clickedCellIndex, setClickedCellIndex] = hooks.useState<number>("clickedCellIndex");

        const getLabel = (id: number) => labelData.find(l => l.id == id) || DEFAULT_LABEL;

        return <CellColumn
            kernel={kernel}
            getLabel={getLabel}
            getTooltip={(kernel, cell) => `Stage: ${getLabel(cell.mainLabel).title}<br/>Lines: ${cell.sourceLinesCount}`}
            clickListener={idx => setClickedCellIndex(idx)}
            style={{}}
        />;
    }
}

customElements.define("cell-column-element", CellColumnElement);