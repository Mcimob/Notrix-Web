import React from "react";
import {FixedSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";
import CellColumn from "Frontend/src/react/matrix/cell-column";

export type CellData = { sourceLinesCount: number; cellType: number; mainLabel: number };
export type KernelData = { id: string; title: string; currentUrlSlug: string; labelSequenceWithMd: number[]; lengthSequence: number[], isUploaded: boolean };

export type LabelData = {id: number, title: string};

export const DEFAULT_LABEL = {id: -1, title: "None"}

class NotebookMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [items, _setItems] = hooks.useState<KernelData[]>("items", []);
        const [totalItems, _setTotalItems] = hooks.useState<number>("totalItems", 0);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", []);
        const fireKernelClick = hooks.useCustomEvent<string>("kernel-click");
        const fireLoadMoreClick = hooks.useCustomEvent<number>("load-more-click");
        const listRef = React.useRef<FixedSizeList>(null);

        const itemWidth = 28;
        const defaultWidth = 400;

        const getLabel = (id: number) => labelData.find(l => l.id == id) || DEFAULT_LABEL;

        const Column = ({index, style} : {index: number, style: React.CSSProperties}) => {
            if (index >= items.length && items.length < totalItems) {
                return <div className={"flex-column flex-center height-full font-weight-bold"} style={{
                    ...style,
                    border: "2px solid black",
                    width: "24px",
                    margin: "2px",
                    padding: "2px",
                    cursor: "pointer"
                }}
                onClick={() => fireLoadMoreClick(items.length)}>+</div>
            }
            const item = items[index];
            if (!item) {
                return <div style={style}>...</div>;
            }
            return <CellColumn
                kernel={item}
                getLabel={getLabel}
                getTooltip={(kernel, label, numLines) => `Stage: ${getLabel(label).title}<br/>Title: ${kernel.title}<br/>Lines: ${numLines}`}
                clickListener={() => fireKernelClick(item.id)}
                style={style}
                data-kernel-index={index}
            />
        }

        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                (
                    <FixedSizeList
                        ref={listRef}
                        itemSize={itemWidth}
                        height={height || 300}
                        itemCount={Math.min( totalItems, items.length + 1)}
                        width={width || defaultWidth}
                        layout={"horizontal"}
                        initialScrollOffset={Math.max(0, (items.length - 49) * itemWidth - (width || defaultWidth))}>
                        {Column}
                    </FixedSizeList>
                )} />
        </div>
    }
}

customElements.define("notebook-matrix", NotebookMatrix);