import React from "react";
import {FixedSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";

type CellData = { sourceLinesCount: number; cellType: number; mainLabel: number };
type KernelData = { title: string; currentUrlSlug: string; labelSequence: number[]; cells: CellData[] };

type LabelData = {id: number, title: string};

class NotebookMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [items, _setItems] = hooks.useState<KernelData[]>("items", []);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])

        const Cells = (item: KernelData) => {
            const result = [];
            const cells = item.cells;
            const labelSequence = item.labelSequence;
            let sequenceIndex = 0;
            for (let cell of cells) {
                let className = `width-full cell stage-${cell.mainLabel}`;

                if (cell.mainLabel != -1 && labelSequence.length != 0) {
                    if (labelSequence[sequenceIndex] != cell.mainLabel) {
                        sequenceIndex++;
                    }
                    if (sequenceIndex + 1 < labelSequence.length) {
                        className = className.concat(` transition-${labelSequence[sequenceIndex]}-${labelSequence[sequenceIndex + 1]}`);
                    }
                    if (sequenceIndex - 1 >= 0) {
                        className = className.concat(` transition-${labelSequence[sequenceIndex - 1]}-${labelSequence[sequenceIndex]}`);
                    }
                }

                result.push(<div
                    style={{
                        backgroundColor: `var(--clr-stage-${cell.mainLabel}, white)`,
                        height: "10px",
                        flexShrink: 0,
                        display: cell.mainLabel == -1 ? "var(--display-md)" : "block",
                        border: cell.mainLabel == -1 ? "1px solid #bbb" : `1px solid var(--clr-stage-${cell.mainLabel})`,
                    }}
                    className={className}
                    data-tooltip={`Stage: ${labelData.find(l => l.id == cell.mainLabel)?.title || "None"}<br/>Title: ${item.title}<br/>Lines: ${cell.sourceLinesCount}`} />);
            }

            return result;
        }

        const Column = ({index, style} : {index: number, style: React.CSSProperties}) => {
            const item = items[index];
            if (!item) {
                return <div style={style}>...</div>;
            }
            return <div
                style={{...style, width: "20px", gap: "1px", margin: "2px"}}
                className={"flex-column"}
                kernel-index={index}>
                {Cells(item)}
            </div>;
        }

        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                (<FixedSizeList itemSize={24} height={height || 300} itemCount={items.length} width={width || 400} layout={"horizontal"}>
                    {Column}
                </FixedSizeList>
                )} />
        </div>
    }
}

customElements.define("notebook-matrix", NotebookMatrix);