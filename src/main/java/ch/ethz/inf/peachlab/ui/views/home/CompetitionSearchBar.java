package ch.ethz.inf.peachlab.ui.views.home;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public class CompetitionSearchBar extends TextField {

    public CompetitionSearchBar() {
        setPrefixComponent(VaadinIcon.SEARCH.create());
        setPlaceholder("Name, Tags, ...");
        setLabel("Search");
        addClassNames(STYLE_BACKGROUND_WHITE, STYLE_PADDING_M, STYLE_WIDTH_FULL);
    }

}
