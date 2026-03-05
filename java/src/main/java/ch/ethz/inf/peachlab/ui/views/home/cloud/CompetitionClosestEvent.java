package ch.ethz.inf.peachlab.ui.views.home.cloud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

import java.io.Serial;

@DomEvent("competition-closest")
public class CompetitionClosestEvent extends ComponentEvent<Component> {

    @Serial
    private static final long serialVersionUID = 4784064670823218726L;
    private final String competitionId;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public CompetitionClosestEvent(Component source, boolean fromClient, @EventData("event.detail") String competitionId) {
        super(source, fromClient);
        this.competitionId = competitionId;
    }

    public String getCompetitionId() {
        return competitionId;
    }
}
