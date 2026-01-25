package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UiAsyncUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiAsyncUtils.class);

    private UiAsyncUtils() {}

    public static <T extends Serializable> void callServiceAsync(
        Supplier<ServiceResponse<T>> callable,
        UI ui,
        SerializableConsumer<ServiceResponse<T>> consumer) {

        CompletableFuture
            .supplyAsync(callable)
            .whenComplete((res, err) -> {
                SerializableRunnable uiTask = () -> {
                    ServiceResponse<T> response = res;
                    if (err != null) {
                        LOGGER.error("An error ocurred while executing asnychronously", err);
                        response = ServiceResponse.error();
                    }
                    consumer.accept(response);
                };

                ui.getSession().access(() -> {
                    if (ui.isAttached())
                        uiTask.run();
                });
            });
    }
}
