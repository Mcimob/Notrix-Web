package ch.ethz.inf.peachlab.backend.broadcaster;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

public class ProcessedNotebookBroadcaster {

    static Map<String, BroadcastListener> notebookListeners = new HashMap<>();
    static Map<String, BroadcastListener> competitionListeners = new HashMap<>();

    public static synchronized Registration registerNotebookListener(
        SerializableConsumer<String> listener, String identifier, UI ui) {
        notebookListeners.put(identifier, new BroadcastListener(listener, ui));
        return () -> {
            synchronized (ProcessedNotebookBroadcaster.class) {
                notebookListeners.remove(identifier);
            }
        };
    }

    public static synchronized void broadcastNotebooksDone(String identifier) {
        if (notebookListeners.containsKey(identifier)) {
            BroadcastListener listener = notebookListeners.get(identifier);
            UI ui = listener.ui();
            ui.access(() -> {
                if (ui.isAttached()) {
                    listener.consumer().accept(identifier);
                }
            });
        }
    }

    public static synchronized Registration registerCompetitionListener(
        SerializableConsumer<String> listener, String identifier, UI ui) {
        competitionListeners.put(identifier, new BroadcastListener(listener, ui));
        return () -> {
            synchronized (ProcessedNotebookBroadcaster.class) {
                competitionListeners.remove(identifier);
            }
        };
    }

    public static synchronized void broadcastCompetitionsDone(String identifier) {
        if (competitionListeners.containsKey(identifier)) {
            BroadcastListener listener = competitionListeners.get(identifier);
            UI ui = listener.ui();
            ui.access(() -> {
                if (ui.isAttached()) {
                    listener.consumer().accept(identifier);
                }
            });
        }
    }
}
