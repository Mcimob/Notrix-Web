import React from "react";
import {TransitionProps} from "Frontend/src/react/sidebar/sidebar-types";
import {computeTransitionPosition, setOpacity} from "Frontend/src/react/sidebar/sidebar-utils";


export default function SidebarArrow({fromStage, from, toStage, to, value, maxValue, labelFunction, strokeFunction, countFunction, opacityTargets}: TransitionProps) {
    const targets = ["transition-sidebar path", ...opacityTargets];

    const onPathMouseover = (event: React.MouseEvent<SVGPathElement, MouseEvent>)=> {
        const path = event.currentTarget;
        const transitionClass = Array.from(path.classList).find(cls => cls.startsWith("transition-"));
        if (!transitionClass) return;

        targets.forEach(selector => {
            document.querySelectorAll(selector).forEach(setOpacity("0.1"));
            document.querySelectorAll(`${selector}.${transitionClass}`).forEach(setOpacity("0.9"));
        });
    };

    const onPathMouseleave = (_: React.MouseEvent<SVGRectElement, MouseEvent>) => {
        targets.forEach(selector =>
            document.querySelectorAll(selector).forEach(setOpacity("1")));
    };

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
            onMouseOver={onPathMouseover}
            onMouseLeave={onPathMouseleave}
        />);
}