import {Stage} from "Frontend/src/react/sidebar/sidebar-types";

export const RECT_SPACING = 70;
export const VIEWBOX_WIDTH = 400;
export const RECT_WIDTH = 60;

export const DEFAULT_LABEL = {
    id: 12,
    name: "Other",
    groupName: "Other",
    color: "B3B3B3",
    stroke: "none",
    strokeDasharray: "none"
}

export const setOpacity = (value: string) =>
    (el: Element) => {
        return (el as HTMLElement).style.opacity = value;
}

export const rectHeight = (value: number, maxValue: number) =>
    15 + (value * 45) / maxValue;

type TransitionPosition = {
    x: number;
    y1: number;
    y2: number;
    yDiff: number;
    side: number;
    ctrlX: number;
}

export const computeTransitionPosition = (fromStage: Stage, from: number, toStage: Stage, to: number, maxValue: number, countFunction: (id: number) => number): TransitionPosition =>  {
    const x = VIEWBOX_WIDTH / 2;
    const y1 = from * RECT_SPACING + rectHeight(fromStage.count, maxValue) / 2;
    const y2 = to * RECT_SPACING + rectHeight(toStage.count, maxValue) / 2;
    const yDiff = y2 - y1;
    const side = Math.sign(yDiff);
    const ctrlX = x + side * Math.min(Math.abs(yDiff) * 0.5, 200);

    return {x, y1, y2, yDiff, side, ctrlX};
}