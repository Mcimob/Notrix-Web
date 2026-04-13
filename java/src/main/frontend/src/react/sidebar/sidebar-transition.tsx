import React, {SVGAttributes} from "react";
import {computeTransitionPosition, setOpacity} from "Frontend/src/react/sidebar/sidebar-utils";
import {Label, Stage, TransitionProps} from "Frontend/src/react/sidebar/sidebar-types";

export default function SidebarTransition({
    fromStage,
    from,
    toStage,
    to,
    value,
    maxValue,
    labelFunction,
    strokeFunction,
    countFunction,
    ...props
}: TransitionProps & SVGAttributes<any>) {
    const {x, y1, y2, ctrlX} = computeTransitionPosition(fromStage, from, toStage, to, maxValue, countFunction);

    const strokeWidth = strokeFunction(value);
    const dPath = `
        M ${x},${y1}
        C ${ctrlX},${y1} ${ctrlX},${y2} ${x},${y2}`;

    const tooltipText = `\
<b>${labelFunction(fromStage.id)!.name} -> ${labelFunction(toStage.id)!.name}</b>
Count: ${value}`;

    return (
        <path
            key={`path-${fromStage.id}-${toStage.id}`}
            d={dPath}
            fill="none"
            stroke={`url(#grad-${fromStage.id}-${toStage.id})`}
            strokeWidth={strokeWidth}
            className={`with-hover stage-${fromStage.id} stage-${toStage.id} transition-${fromStage.id}-${toStage.id}`}
            data-tooltip={tooltipText}
            {...props}
        />
    );
}