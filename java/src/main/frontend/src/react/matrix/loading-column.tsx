import React, {CSSProperties} from "react";

type LoadingColumnProps = {
    loading: boolean;
    loadMore: (max: number) => void;
    setLoading: (loading: boolean) => void;
    currentItems: number;
    style: CSSProperties;
} & React.ComponentPropsWithoutRef<'div'>;

export default function LoadingColumn({loading, loadMore, setLoading, currentItems, style, ...props} : LoadingColumnProps) {
    return <div
        className={"flex-column flex-center height-full font-weight-bold"}
        {...props}
        style={{
            ...style,
            border: "2px solid black",
            width: "24px",
            margin: "2px",
            padding: "2px",
            cursor: loading ? "wait" : "pointer"
        }}
        onClick={() => {
            if (!loading)
                loadMore(currentItems);
            setLoading(true);
        }}>{loading ? "..." : "+"}</div>
}