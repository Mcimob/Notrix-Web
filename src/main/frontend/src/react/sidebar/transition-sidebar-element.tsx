import React from "react";
import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import {Props} from "Frontend/src/react/sidebar/sidebar-types";
import SidebarRect from "Frontend/src/react/sidebar/sidebar-rect";
import {DEFAULT_LABEL, RECT_SPACING, VIEWBOX_WIDTH} from "Frontend/src/react/sidebar/sidebar-utils";
import SidebarGradient from "Frontend/src/react/sidebar/sidebar-gradient";
import SidebarTransition from "Frontend/src/react/sidebar/sidebar-transition";
import SidebarArrow from "Frontend/src/react/sidebar/sidebar-arrow";

class TransitionSidebar extends ReactAdapterElement {

    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [data, _setData] = hooks.useState<Props>("data")

        const { stages, transitions, labels } = data;

        const maxValue = Math.max(...stages.map(s => s.count));
        const height = stages.length * RECT_SPACING;
        const minTransitionValue = Math.min(...transitions.flat());
        const maxTransitionValue = Math.max(...transitions.flat());
        const minPathWidth = 2;
        const maxPathWidth = 26;

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

        const label = (id: number) => {
            return labels.find(l => l.id == id) || DEFAULT_LABEL;
        }

        return (
            <svg viewBox={`0 0 ${VIEWBOX_WIDTH} ${height}`} width="100%" height="100%">
                {/* ===================== DEFS ===================== */}
                <defs>
                    {stages.flatMap((fromStage, from) =>
                        stages.map((toStage, to) =>
                            <SidebarGradient
                                fromStage={fromStage}
                                from={from}
                                toStage={toStage}
                                to={to}
                                labelFunction={label}
                            />
                        )
                    )}
                </defs>

                {/* ===================== PATHS ===================== */}
                {stages.flatMap((fromStage, from) =>
                    stages.map((toStage, to) => {
                        const value = transitions[fromStage.id]?.[toStage.id];
                        if (!value || fromStage === toStage) return null;
                        return (<>
                            <SidebarTransition
                                fromStage={fromStage}
                                from={from}
                                toStage={toStage}
                                to={to}
                                value={value}
                                maxValue={maxValue}
                                labelFunction={label}
                                strokeFunction={pathStrokeWidth}
                                countFunction={n => stages.find(s => s.id == n)?.count || 0}
                            />
                            <SidebarArrow
                                fromStage={fromStage}
                                from={from}
                                toStage={toStage}
                                to={to}
                                value={value}
                                maxValue={maxValue}
                                labelFunction={label}
                                strokeFunction={pathStrokeWidth}
                                countFunction={n => stages.find(s => s.id == n)?.count || 0}
                            />
                        </>);
                    })
                )}

                {/* ===================== RECTS ===================== */}
                {stages.map((stage, i) =>
                    <SidebarRect
                        stage={stage}
                        index={i}
                        maxValue={maxValue}
                        labelFunction={label}
                    />
                    )
                }
            </svg>
        );
    }
}

customElements.define("transition-sidebar", TransitionSidebar);