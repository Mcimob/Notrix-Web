package ch.ethz.inf.peachlab.ui.views.home.cloud;

import ch.ethz.inf.peachlab.model.dto.CompetitionDTO;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.react.ReactAdapterComponent;

import java.io.Serial;
import java.util.Collection;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;

@Tag("competition-map-element")
@JsModule("./src/react/pointcloud/competition-map-element.tsx")
@NpmPackage(value = "d3", version="7.9.0")
@NpmPackage(value = "@types/d3", version="7.4.3", dev = true)
@NpmPackage(value = "react-virtualized-auto-sizer", version="2.0.2")
public class CompetitionCloud extends ReactAdapterComponent {

    @Serial
    private static final long serialVersionUID = -6697361866120448118L;

    public CompetitionCloud() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_BACKGROUND_WHITE, STYLE_FLEX_ROW, STYLE_FLEX_CENTER);
    }

    public void setCompetitions(Collection<CompetitionDTO> competitions) {
        setState("competitions", competitions);
    }

    public void addCompetitionClickedListener(ComponentEventListener<CompetitionClickEvent> listener) {
        addListener(CompetitionClickEvent.class, listener);
    }

    public void addCompetitionClosestListener(ComponentEventListener<CompetitionClosestEvent> listener) {
        addListener(CompetitionClosestEvent.class, listener);
    }
}
