package ch.ethz.inf.peachlab.backend.broadcaster;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

public class ProcessingCompetitionBroadcaster extends AbstractBroadcaster {

    static Map<String, BroadcastListener<String>> competitionListeners = new HashMap<>();

    public static synchronized Registration register(
        SerializableConsumer<String> listener, String identifier, UI ui) {
        return AbstractBroadcaster.register(listener, identifier, ui, competitionListeners);
    }

    public static synchronized void broadcast(String identifier) {
        AbstractBroadcaster.broadcast(identifier, competitionListeners, identifier);
    }
}
