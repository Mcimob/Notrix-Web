import React from "react";
import {FixedSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";
import CellColumn from "Frontend/src/react/matrix/cell-column";

export type CellData = { sourceLinesCount: number; cellType: number; mainLabel: number };
export type KernelData = { id: string; title: string; currentUrlSlug: string; labelSequence: number[]; cells: CellData[], isUploaded: boolean };

export type LabelData = {id: number, title: string};

export const DEFAULT_LABEL = {id: -1, title: "None"}

class NotebookMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [items, _setItems] = hooks.useState<KernelData[]>("items", []);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])
        const fireKernelClick = hooks.useCustomEvent<string>("kernel-click")

        const getLabel = (id: number) => labelData.find(l => l.id == id) || DEFAULT_LABEL;

        const Column = ({index, style} : {index: number, style: React.CSSProperties}) => {
            const item = items[index];
            if (!item) {
                return <div style={style}>...</div>;
            }
            return <CellColumn
                kernel={item}
                getLabel={getLabel}
                getTooltip={(kernel, cell) => `Stage: ${getLabel(cell.mainLabel).title}<br/>Title: ${kernel.title}<br/>Lines: ${cell.sourceLinesCount}`}
                clickListener={() => fireKernelClick(item.id)}
                style={style}
                data-kernel-index={index}
            />
        }

        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                (<FixedSizeList itemSize={28} height={height || 300} itemCount={items.length} width={width || 400} layout={"horizontal"}>
                    {Column}
                </FixedSizeList>
                )} />
        </div>
    }
}

customElements.define("notebook-matrix", NotebookMatrix);