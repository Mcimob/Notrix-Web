import {ReactAdapterElement, RenderHooks} from "Frontend/generated/flow/ReactAdapter";
import React from "react";
import CompetitionMap, {Competition} from "Frontend/src/react/pointcloud/competition-map";
import {AutoSizer, AutoSizerChildProps} from "react-virtualized-auto-sizer";

class CompetitionMapElement extends ReactAdapterElement {

    protected render(hooks: RenderHooks): React.ReactElement | null {
        const [competitions, _setCompetitions] = hooks.useState<Competition[]>("competitions", []);
        const fireCompetitionClick = hooks.useCustomEvent<string>("competition-click");
        return <div className={"width-full height-full"}>
            <AutoSizer ChildComponent={({width, height}: AutoSizerChildProps) =>
                <CompetitionMap
                    clickListener={id => fireCompetitionClick(id)}
                    width={width}
                    height={height}
                    data={competitions}
                />}/>
        </div>;
    }
}

customElements.define("competition-map-element", CompetitionMapElement);