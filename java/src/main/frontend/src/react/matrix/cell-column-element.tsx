import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import React from "react";
import {DEFAULT_LABEL, KernelData, LabelData} from "Frontend/src/react/matrix/notebook-matrix";
import CellColumn from "Frontend/src/react/matrix/cell-column";
import {addListener} from "Frontend/src/react/matrix/listener-utils";


class CellColumnElement extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [kernel, _setKernel] = hooks.useState<KernelData>("kernel");
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])
        const [_clickedCellIndex, setClickedCellIndex] = hooks.useState<number>("clickedCellIndex");

        const [selectedLabel, setSelectedLabel] = React.useState<number>(-1);
        const [selectedTransition, setSelectedTransition] = React.useState<number[] | undefined>();

        addListener("label-selected", e => {
            const customEvent = e as CustomEvent;
            let selectedLabel = customEvent.detail;
            setSelectedLabel(selectedLabel);
        }, document);

        addListener("transition-selected", e => {
            const customEvent = e as CustomEvent;
            let selectedTransition = customEvent.detail;
            setSelectedTransition(selectedTransition);
        }, document);

        const getLabel = (id: number) => labelData.find(l => l.id == id) || DEFAULT_LABEL;

        return <CellColumn
            kernel={kernel}
            getLabel={getLabel}
            getTooltip={(kernel, label, numLines) => `Stage: ${getLabel(label).title}<br/>Lines: ${numLines}`}
            clickListener={idx => setClickedCellIndex(idx)}
            style={{}}
            selectedLabel={selectedLabel}
            selectedTransition={selectedTransition}
        />;
    }
}

customElements.define("cell-column-element", CellColumnElement);