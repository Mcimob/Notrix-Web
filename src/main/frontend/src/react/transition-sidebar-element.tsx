import React from "react";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";


/**
 * Props coming from Vaadin via setState(...)
 */
type Props = {
    stages: {
        id: number;
        count: number;
    }[];
    transitions: number[][];
    labels: {
        id: number;
        name: string;
        groupName: string;
        color: string;
        stroke: string;
        strokeDasharray: string
    }[];
};

class TransitionSidebar extends ReactAdapterElement {
    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [data, setData] = hooks.useState<Props>("data")

        const { stages, transitions, labels } = data;
        const RECT_SPACING = 70;
        const VIEWBOX_WIDTH = 400;
        const RECT_WIDTH = 60;

        const maxValue = Math.max(...stages.map(s => s.count));
        const height = stages.length * RECT_SPACING;

        const rectHeight = (value: number) =>
            15 + (value * 45) / maxValue;

        const label = (id: number)=> {
            return labels.find(l => l.id == id);
        }

        return (
            <svg viewBox={`0 0 ${VIEWBOX_WIDTH} ${height}`} width="100%" height="100%">
                {/* ===================== DEFS ===================== */}
                <defs>
                    {stages.flatMap((fromStage, from) =>
                        stages.map((toStage, to) => {
                            if (from === to) return null;

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
                                        stopColor={label(fromStage.id)!.color}
                                        stopOpacity="1"
                                    />
                                    <stop
                                        offset="100%"
                                        stopColor={label(toStage.id)!.color}
                                        stopOpacity="0.25"
                                    />
                                </linearGradient>
                            );
                        })
                    )}
                </defs>

                {/* ===================== PATHS ===================== */}
                {stages.flatMap((from) =>
                    stages.map((to) => {
                        const value = transitions[from.id]?.[to.id];
                        if (!value || from === to) return null;

                        const x = VIEWBOX_WIDTH / 2;
                        const y1 = from.id * RECT_SPACING + rectHeight(stages[from.id].count) / 2;
                        const y2 = to.id * RECT_SPACING + rectHeight(stages[to.id].count) / 2;
                        const side = y2 > y1 ? 1 : -1;
                        const ctrlX = x + side * Math.min(Math.abs(y2 - y1) * 0.5, 200);

                        const d = `
            M ${x},${y1}
            C ${ctrlX},${y1} ${ctrlX},${y2} ${x},${y2}
          `;

                        return (
                            <path
                                key={`path-${from.id}-${to.id}`}
                                d={d}
                                fill="none"
                                stroke={`url(#grad-${from.id}-${to.id})`}
                                strokeWidth={5}
                                className={"with-hover"}
                                data-tooltip={`<b>${label(from.id)!.name} -> ${label(to.id)!.name}</b><br/>Count: ${value}`}
                            />
                        );
                    })
                )}

                {/* ===================== RECTS ===================== */}
                {stages.map((stage, i) => {
                    const h = rectHeight(stage.count);

                    return (
                        <rect
                            key={stage.id}
                            x={(VIEWBOX_WIDTH - RECT_WIDTH) / 2}
                            y={i * RECT_SPACING}
                            width={RECT_WIDTH}
                            height={h}
                            rx={3}
                            ry={3}
                            fill={label(stage.id)!.color}
                            stroke={label(stage.id)!.stroke}
                            strokeWidth={3}
                            strokeDasharray={label(stage.id)!.strokeDasharray}
                            className={"with-hover"}
                            data-tooltip={`<b>${label(stage.id)!.name}</b><br/>${label(stage.id)!.groupName}<br/>Count: ${stage.count}`}
                        />
                    );
                })}
            </svg>
        );
    }
}

customElements.define("transition-sidebar", TransitionSidebar);