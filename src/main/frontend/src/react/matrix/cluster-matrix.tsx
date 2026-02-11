import React from "react";
import {VariableSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";
import CellColumn from "Frontend/src/react/matrix/cell-column";
import {KernelData} from "Frontend/src/react/matrix/notebook-matrix";

export type ClusterData = {clusterId: number, localClusterId?: number, kernels: KernelData[]}

export type LabelData = {id: number, title: string};

export const DEFAULT_LABEL = {id: -1, title: "None"}

class CLusterMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [items, _setItems] = hooks.useState<ClusterData[]>("items", []);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])
        const [_clickedKernelId, setClickedKernelId] = hooks.useState<number>("clickedKernelId", -1);
        const [clickedClusterId, setClickedClusterId] = hooks.useState<number>("clickedClusterId", -1);

        const getLabel = (id: number) => labelData.find(l => l.id == id) || DEFAULT_LABEL;

        const getItemSize = (index: number) => Math.max(3, items[index].kernels.length) * 24 + 12

        const Cluster = ({index, style} : {index: number, style: React.CSSProperties}) => {

            const item = items[index];
            if (!item) {
                return <div style={style}>...</div>;
            }
            const textColor = clickedClusterId == item.clusterId ? "green" : "black";
            return <div
                style={style}
                className={"flex-column flex-align-center"}
                onClick={() => {
                    if (clickedClusterId == item.clusterId) {
                        setClickedClusterId(-1);
                    } else {
                        setClickedClusterId(item.clusterId);
                    }
                }}
            >
                <span style={{color: textColor}} className={"font-size-s text-wrap-no"}>Cluster {item.localClusterId}</span>
                <span style={{color: textColor}} className={"font-size-s text-wrap-no"}>{item.kernels.length} Notebooks</span>
                <hr style={{backgroundColor: textColor, height: "5px"}}/>
                <div className={"flex-row"}>
                    {item.kernels.map(kernel => <CellColumn
                        kernel={kernel}
                        getLabel={getLabel}
                        getTooltip={(kernel, cell) => `Stage: ${getLabel(cell.mainLabel).title}<br/>Title: ${kernel.title}<br/>Lines: ${cell.sourceLinesCount}`}
                        style={{}}
                        clickListener={() => setClickedKernelId(kernel.id)}
                        data-kernel-index={index}
                    />)}
                </div>
            </div>
        }

        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                (<VariableSizeList itemSize={getItemSize} height={height || 300} itemCount={items.length} width={width || 400} layout={"horizontal"}>
                        {Cluster}
                    </VariableSizeList>
                )} />
        </div>
    }
}

customElements.define("cluster-matrix", CLusterMatrix);