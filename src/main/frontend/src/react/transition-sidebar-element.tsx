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
        const minTransitionValue = Math.min(...transitions.flat());
        const maxTransitionValue = Math.max(...transitions.flat());
        const minPathWidth = 2;
        const maxPathWidth = 26;

        const rectHeight = (value: number) =>
            15 + (value * 45) / maxValue;

        const pathStrokeWidth = (count: number) => {
            if (count <= 0)
                return 0;
            if (maxTransitionValue <= 5) {
                // 离散情况直接写死
                return [0, 2, 4][count] || 5;
            }
            const t = (count - minTransitionValue) / (maxTransitionValue - minTransitionValue);
            return minPathWidth + Math.pow(t, 0.4) * (maxPathWidth - minPathWidth);
        }

        const label = (id: number)=> {
            return labels.find(l => l.id == id);
        }

        const setOpacity = (value: string) =>
            (el: Element) => {
                return (el as HTMLElement).style.opacity = value;
            }

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

        const onPathMouseover = (event: React.MouseEvent<SVGPathElement, MouseEvent>)=> {
            const path = event.currentTarget;
            const transitionClass = Array.from(path.classList).find(cls => cls.startsWith("transition-"));
            if (!transitionClass) return;

            document.querySelectorAll("#notebook-matrix .cell").forEach(setOpacity("0.1"));
            document.querySelectorAll("transition-sidebar path").forEach(setOpacity("0.1"));

            document.querySelectorAll(`#notebook-matrix .${transitionClass}`).forEach(setOpacity("0.9"));
            path.style.opacity = "0.9";
        };

        const onPathMouseleave = (_: React.MouseEvent<SVGRectElement, MouseEvent>) => {
            document.querySelectorAll("#notebook-matrix .cell").forEach(setOpacity("1"));
            document.querySelectorAll("transition-sidebar path").forEach(setOpacity("1"))
        };

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
                                strokeWidth={pathStrokeWidth(value)}
                                className={`with-hover stage-${from.id} stage-${to.id} transition-${from.id}-${to.id}`}
                                data-tooltip={`<b>${label(from.id)!.name} -> ${label(to.id)!.name}</b><br/>Count: ${value}`}
                                onMouseOver={onPathMouseover}
                                onMouseLeave={onPathMouseleave}
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
                            className={`with-hover stage-${stage.id}`}
                            data-tooltip={`<b>${label(stage.id)!.name}</b><br/>${label(stage.id)!.groupName}<br/>Count: ${stage.count}`}
                            onMouseOver={onRectMouseover}
                            onMouseLeave={onRectMouseLeave}
                        />
                    );
                })}
            </svg>
        );
    }
}

customElements.define("transition-sidebar", TransitionSidebar);