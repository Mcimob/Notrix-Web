import React from "react";
import {TransitionProps} from "Frontend/src/react/sidebar/sidebar-types";
import {computeTransitionPosition} from "Frontend/src/react/sidebar/sidebar-utils";


export default function SidebarArrow({fromStage, from, toStage, to, value, maxValue, labelFunction, strokeFunction, countFunction}: TransitionProps) {

    const {x, y1, yDiff, side, ctrlX} = computeTransitionPosition(fromStage, from, toStage, to, maxValue, countFunction);

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
            key={`arrow-${fromStage.id}-${toStage.id}`}
            d={dArrow}
            fill={labelFunction(fromStage.id)!.color}
            stroke={labelFunction(fromStage.id)!.color}
            strokeWidth={1.2}
            strokeLinejoin={"round"}
            transform={`translate(${x + (ctrlX - x) * 0.75}, ${y1 + yDiff / 2}) rotate(${(side - 1) * 90})`}
            className={`stage-${fromStage.id} stage-${toStage.id} transition-${fromStage.id}-${toStage.id}`}
        />);
}