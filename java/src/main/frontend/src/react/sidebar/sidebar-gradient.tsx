import {Label, Stage} from "Frontend/src/react/sidebar/sidebar-types";
import React from "react";
import {RECT_SPACING, VIEWBOX_WIDTH} from "Frontend/src/react/sidebar/sidebar-utils";

type GradientProps = {
    fromStage: Stage;
    from: number;
    toStage: Stage;
    to: number;
    labelFunction: (value: number) => Label;
}

export default function SidebarGradient({fromStage, from, toStage, to, labelFunction}: GradientProps) {
    if (from == to) return null;

    return (
        <linearGradient
            key={`grad-${fromStage.id}-${toStage.id}`}
            id={`grad-${fromStage.id}-${toStage.id}`}
            gradientUnits="userSpaceOnUse"
            x1={VIEWBOX_WIDTH / 2}
            y1={from * RECT_SPACING}
            x2={VIEWBOX_WIDTH / 2}
            y2={to * RECT_SPACING}
        >
            <stop
                offset="0%"
                stopColor={labelFunction(fromStage.id)!.color}
                stopOpacity="1"
            />
            <stop
                offset="100%"
                stopColor={labelFunction(toStage.id)!.color}
                stopOpacity="0.25"
            />
        </linearGradient>
    );
}