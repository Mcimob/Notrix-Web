import {Label, Stage} from "Frontend/src/react/sidebar/sidebar-types";
import React from "react";
import {
    RECT_SPACING,
    RECT_WIDTH,
    rectHeight,
    setOpacity,
    VIEWBOX_WIDTH
} from "Frontend/src/react/sidebar/sidebar-utils";

type RectProps = {
    stage: Stage;
    index: number;
    maxValue: number;
    labelFunction: (value: number) => Label;
};

export default function SidebarRect({stage, index, maxValue, labelFunction} : RectProps) {

    const onRectMouseover = (event: React.MouseEvent<SVGRectElement, MouseEvent>) => {
        const rect = event.currentTarget;
        const stageClass = Array.from(rect.classList).find(cls => cls.startsWith("stage-"));
        if (!stageClass) return;

        // Highlight all rects with this stage class
        document.querySelectorAll("#notebook-matrix .cell").forEach(setOpacity("0.1"));
        document.querySelectorAll("transition-sidebar path").forEach(setOpacity("0.1"));

        document.querySelectorAll(`#notebook-matrix .${stageClass}`).forEach(setOpacity("0.9"));
        document.querySelectorAll(`transition-sidebar path.${stageClass}`).forEach(setOpacity("0.9"));
    };

    const onRectMouseLeave = (_: React.MouseEvent<SVGRectElement, MouseEvent>) => {
        document.querySelectorAll("#notebook-matrix .cell").forEach(setOpacity("1"));
        document.querySelectorAll("transition-sidebar path").forEach(setOpacity("1"));
    };

    const toolTipText = `\
<b>${labelFunction(stage.id)!.name}</b>
${labelFunction(stage.id)!.groupName}
Count: ${stage.count}`

    return (
        <rect
            key={stage.id}
            x={(VIEWBOX_WIDTH - RECT_WIDTH) / 2}
            y={index * RECT_SPACING}
            width={RECT_WIDTH}
            height={rectHeight(stage.count, maxValue)}
            rx={3}
            ry={3}
            fill={labelFunction(stage.id)!.color}
            stroke={labelFunction(stage.id)!.stroke}
            strokeWidth={3}
            strokeDasharray={labelFunction(stage.id)!.strokeDasharray}
            className={`with-hover stage-${stage.id}`}
            data-tooltip={toolTipText}
            onMouseOver={onRectMouseover}
            onMouseLeave={onRectMouseLeave}
        />
    );
}