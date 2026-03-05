import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import React from "react";
import CompetitionMap, {Competition} from "Frontend/src/react/pointcloud/competition-map";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";

class CompetitionMapElement extends ReactAdapterElement {

    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [competitions, _setCompetitions] = hooks.useState<Competition[]>("competitions", []);
        const fireCompetitionClick = hooks.useCustomEvent<string>("competition-click");
        const fireCompetitionClosest = hooks.useCustomEvent<string>("competition-closest");
        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                <CompetitionMap
                    clickListener={fireCompetitionClick}
                    closestChangeListener={fireCompetitionClosest}
                    width={width || 300}
                    height={height || 500}
                    data={competitions}
                />}/>
        </div>;
    }
}

customElements.define("competition-map-element", CompetitionMapElement);