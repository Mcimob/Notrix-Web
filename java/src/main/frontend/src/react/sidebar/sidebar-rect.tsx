import {Label, Stage} from "Frontend/src/react/sidebar/sidebar-types";
import React from "react";
import {
    RECT_SPACING,
    RECT_WIDTH,
    rectHeight,
    setOpacity,
    VIEWBOX_WIDTH
} from "Frontend/src/react/sidebar/sidebar-utils";
import {pointer} from "d3";

type RectProps = {
    stage: Stage;
    index: number;
    maxValue: number;
    labelFunction: (value: number) => Label;
    currentlySelectedLabel: number;
    setCurrentlySelectedLabel: (label: number) => void;
    currentlyClicked: boolean;
    setCurrentlyClicked: (clicked: boolean ) => void;
};

export default function SidebarRect({
    stage,
    index,
    maxValue,
    labelFunction,
    currentlySelectedLabel,
    setCurrentlySelectedLabel,
    currentlyClicked,
    setCurrentlyClicked
} : RectProps) {

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
            cursor={"pointer"}
            onMouseOver={() => {
                if (!currentlyClicked) {
                    setCurrentlySelectedLabel(stage.id);
                }
            }}
            onMouseLeave={() => {
                if (!currentlyClicked) {
                    setCurrentlySelectedLabel(-1);
                }
            }}
            onClick={() => {
                if (currentlyClicked) {
                    setCurrentlyClicked(currentlySelectedLabel != stage.id);
                    setCurrentlySelectedLabel(stage.id);
                } else {
                    setCurrentlyClicked(true);
                }
            }}
        />
    );
}