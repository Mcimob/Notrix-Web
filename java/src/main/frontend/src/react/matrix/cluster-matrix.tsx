import React from "react";
import {VariableSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";
import CellColumn from "Frontend/src/react/matrix/cell-column";
import {KernelData} from "Frontend/src/react/matrix/notebook-matrix";

export type ClusterData = {clusterId: number, localClusterId: number, kernels: KernelData[]}

export type LabelData = {id: number, title: string};

export const DEFAULT_LABEL = {id: -1, title: "None"}

class CLusterMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [items, _setItems] = hooks.useState<ClusterData[]>("items", []);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", [])

        const [highlightedClusterId, setHighlightedClusterId] =
            React.useState<number | null>(null);

        const fireClusterClick = hooks.useCustomEvent<string>("cluster-click");
        const fireKernelClick = hooks.useCustomEvent<string>("kernel-click")

        const listRef = React.useRef<VariableSizeList>(null);
        const scrollOffset = React.useRef(0);

        const getLabel = React.useCallback(
            (id: number) => labelData.find(l => l.id === id) || DEFAULT_LABEL,
            [labelData]
        );

        const getItemSize = React.useCallback(
            (index: number) =>
                Math.max(3, items[index]?.kernels.length ?? 0) * 28 + 12,
            [items]
        );

        const Cluster = React.useCallback(
            ({ index, style }: { index: number; style: React.CSSProperties }) => {
                const item = items[index];
                if (!item) return <div style={style}>...</div>;

                const isHighlighted = highlightedClusterId === item.localClusterId;
                const textColor = isHighlighted ? "green" : "black";

                const onClick = () => {
                    const next =
                        isHighlighted ? null : item.localClusterId;

                    setHighlightedClusterId(next);
                    fireClusterClick(next?.toFixed() ?? "-1");
                };

                return (
                    <div
                        style={style}
                        className="flex-column flex-align-center"
                        onClick={onClick}
                    >
                        <span style={{ color: textColor }} className="font-size-s">
                            Cluster {item.localClusterId}
                        </span>
                        <span style={{ color: textColor }} className="font-size-s">
                            {item.kernels.length} Notebooks
                        </span>
                        <hr style={{ backgroundColor: textColor, height: "5px" }} />
                        <div className="flex-row">
                            {item.kernels.map(kernel => (
                                <CellColumn
                                    key={kernel.id}
                                    kernel={kernel}
                                    getLabel={getLabel}
                                    clickListener={() => fireKernelClick(kernel.id)}
                                    getTooltip={(kernel, cell) => `Stage: ${getLabel(cell.mainLabel).title}<br/>Title: ${kernel.title}<br/>Lines: ${cell.sourceLinesCount}`}
                                    style={{}}
                                />
                            ))}
                        </div>
                    </div>
                );
            },
            [
                items,
                highlightedClusterId,
                fireClusterClick,
                fireKernelClick,
                getLabel
            ]
        );

        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                (<VariableSizeList
                        ref={listRef}
                        initialScrollOffset={scrollOffset.current}
                        itemSize={getItemSize}
                        height={height || 300}
                        itemCount={items.length}
                        width={width || 400}
                        layout={"horizontal"}
                        itemKey={index => items[index].clusterId}
                        onScroll={({ scrollOffset: offset }) => {
                            scrollOffset.current = offset;
                            console.log(offset);
                        }}
                    >
                        {Cluster}
                    </VariableSizeList>
                )} />
        </div>
    }
}

customElements.define("cluster-matrix", CLusterMatrix);