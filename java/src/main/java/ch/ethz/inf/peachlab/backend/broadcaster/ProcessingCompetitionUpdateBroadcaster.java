package ch.ethz.inf.peachlab.backend.broadcaster;

import ch.ethz.inf.peachlab.model.rest.ProcessingStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

public class ProcessingCompetitionUpdateBroadcaster extends AbstractBroadcaster {

    static Map<String, BroadcastListener<ProcessingStatus>> competitionListeners = new HashMap<>();

    public static synchronized Registration register(
        SerializableConsumer<ProcessingStatus> listener, String identifier, UI ui) {
        return AbstractBroadcaster.register(listener, identifier, ui, competitionListeners);
    }

    public static synchronized void broadcast(String identifier, ProcessingStatus status) {
        AbstractBroadcaster.broadcast(identifier, competitionListeners, status);
    }
}
