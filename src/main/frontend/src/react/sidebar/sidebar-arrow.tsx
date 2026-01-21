import React from "react";
import {Label, Stage} from "Frontend/src/react/sidebar/sidebar-types";
import {computeTransitionPosition} from "Frontend/src/react/sidebar/sidebar-utils";

type TransitionProps = {
    from: Stage;
    to: Stage;
    value: number;
    maxValue: number
    labelFunction: (value: number) => Label;
    strokeFunction: (value: number) => number;
    countFunction: (id: number) => number;
}

export default function SidebarArrow({from, to, value, maxValue, labelFunction, strokeFunction, countFunction}: TransitionProps) {

    const {x, y1, yDiff, side, ctrlX} = computeTransitionPosition(from, to, maxValue, countFunction);

    const strokeWidth = strokeFunction(value);
    const arrowSize = Math.max(8, strokeWidth * 0.5);
    const dArrow = `
        M 0 ${arrowSize / 2} 
        L ${-arrowSize / 2} ${-arrowSize / 2} 
        L ${arrowSize / 2} ${-arrowSize / 2} 
        Z
    `;

    return (
        <path
            key={`arrow-${from.id}-${to.id}`}
            d={dArrow}
            fill={labelFunction(from.id)!.color}
            stroke={labelFunction(from.id)!.color}
            strokeWidth={1.2}
            strokeLinejoin={"round"}
            transform={`translate(${x + (ctrlX - x) * 0.75}, ${y1 + yDiff / 2}) rotate(${(side - 1) * 90})`}
            className={`stage-${from.id} stage-${to.id} transition-${from.id}-${to.id}`}
        />);
}