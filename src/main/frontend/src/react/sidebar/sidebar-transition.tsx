import React from "react";
import {computeTransitionPosition, setOpacity} from "Frontend/src/react/sidebar/sidebar-utils";
import {Label, Stage} from "Frontend/src/react/sidebar/sidebar-types";

type TransitionProps = {
    from: Stage;
    to: Stage;
    value: number;
    maxValue: number
    labelFunction: (value: number) => Label;
    strokeFunction: (value: number) => number;
    countFunction: (id: number) => number;
}

export default function SidebarTransition({from, to, value, maxValue, labelFunction, strokeFunction, countFunction}: TransitionProps) {
    const onPathMouseover = (event: React.MouseEvent<SVGPathElement, MouseEvent>)=> {
        const path = event.currentTarget;
        const transitionClass = Array.from(path.classList).find(cls => cls.startsWith("transition-"));
        if (!transitionClass) return;

        document.querySelectorAll("notebook-matrix .cell").forEach(setOpacity("0.1"));
        document.querySelectorAll("transition-sidebar path").forEach(setOpacity("0.1"));

        document.querySelectorAll(`notebook-matrix .${transitionClass}`).forEach(setOpacity("0.9"));
        document.querySelectorAll(`transition-sidebar .${transitionClass}`).forEach(setOpacity("0.9"));
    };

    const onPathMouseleave = (_: React.MouseEvent<SVGRectElement, MouseEvent>) => {
        document.querySelectorAll("notebook-matrix .cell").forEach(setOpacity("1"));
        document.querySelectorAll("transition-sidebar path").forEach(setOpacity("1"))
    };

    const {x, y1, y2, ctrlX} = computeTransitionPosition(from, to, maxValue, countFunction);

    const strokeWidth = strokeFunction(value);
    const dPath = `
        M ${x},${y1}
        C ${ctrlX},${y1} ${ctrlX},${y2} ${x},${y2}`;

    const tooltipText = `\
<b>${labelFunction(from.id)!.name} -> ${labelFunction(to.id)!.name}</b>
Count: ${value}`;

    return (
        <path
            key={`path-${from.id}-${to.id}`}
            d={dPath}
            fill="none"
            stroke={`url(#grad-${from.id}-${to.id})`}
            strokeWidth={strokeWidth}
            className={`with-hover stage-${from.id} stage-${to.id} transition-${from.id}-${to.id}`}
            data-tooltip={tooltipText}
            onMouseOver={onPathMouseover}
            onMouseLeave={onPathMouseleave}
        />
    );
}