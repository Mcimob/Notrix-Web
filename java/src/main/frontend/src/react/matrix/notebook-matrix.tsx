import React from "react";
import {FixedSizeList} from "react-window";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";
import CellColumn from "Frontend/src/react/matrix/cell-column";
import LoadingColumn from "Frontend/src/react/matrix/loading-column";

export type CellData = { sourceLinesCount: number; cellType: number; mainLabel: number };
export type KernelData = { id: string; title: string; currentUrlSlug: string; labelSequenceWithMd: number[]; lengthSequence: number[], isUploaded: boolean };

export type LabelData = {id: number, title: string};

export const DEFAULT_LABEL = {id: -1, title: "None"}

class NotebookMatrix extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [totalItems, _setTotalItems] = hooks.useState<number>("totalItems", 0);
        const [labelData, _setLabelData] = hooks.useState<LabelData[]>("labelData", []);

        const fireKernelClick = hooks.useCustomEvent<string>("kernel-click");
        const fireLoadMoreClick = hooks.useCustomEvent<number>("load-more-click");

        const listRef = React.useRef<FixedSizeList>(null);
        const scrollOffset = React.useRef(0);
        const [currentlyLoading, setCurrentlyLoading] = React.useState<boolean>(true);
        const [items, setItems] = React.useState<KernelData[]>([]);

        React.useEffect(() => {
            const handler = (e: Event) => {
                const customEvent = e as CustomEvent;
                console.log("Custom Event: ", customEvent);
                let newItems = customEvent.detail;
                if (typeof newItems === "string")
                    newItems = JSON.parse(newItems);

                setItems(prev => [...prev, ...newItems]);
                setCurrentlyLoading(false);
            }
            this.addEventListener("append-items", handler);
            return () => {
                this.removeEventListener("append-items", handler)
            }
        }, []);

        React.useEffect(() => {
            const handler = () => {
                setItems([]);
            }
            this.addEventListener("clear-items", handler);
            return () => {
                this.removeEventListener("clear-items", handler);
            };
        }, []);

        const getLabel = React.useCallback(
            (id: number) => labelData.find(l => l.id === id) || DEFAULT_LABEL,
            [labelData]
        );

        const Column = ({index, style} : {index: number, style: React.CSSProperties}) => {
            if (index >= items.length && items.length < totalItems) {
                return <LoadingColumn
                    loading={currentlyLoading}
                    loadMore={fireLoadMoreClick}
                    setLoading={setCurrentlyLoading}
                    currentItems={items.length}
                    style={style}
                />
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
                        itemSize={28}
                        height={height || 300}
                        itemCount={Math.min( totalItems, items.length + 1)}
                        width={width || 400}
                        layout={"horizontal"}
                        initialScrollOffset={scrollOffset.current}
                        onScroll={({scrollOffset: offset}) => {
                            scrollOffset.current = offset;
                        }}
                        overscanCount={5}
                    >
                        {Column}
                    </FixedSizeList>
                )} />
        </div>
    }
}

customElements.define("notebook-matrix", NotebookMatrix);