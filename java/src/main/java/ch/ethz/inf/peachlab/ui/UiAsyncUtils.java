package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UiAsyncUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiAsyncUtils.class);

    private UiAsyncUtils() {}

    public static <T extends Serializable> void callServiceAsync(
        Supplier<ServiceResponse<? extends T>> callable,
        UI ui,
        SerializableConsumer<ServiceResponse<? extends T>> consumer) {

        CompletableFuture
            .supplyAsync(callable)
            .whenComplete((res, err) -> {
                SerializableRunnable uiTask = () -> {
                    ServiceResponse<? extends T> response = res;
                    if (err != null) {
                        LOGGER.error("An error ocurred while executing asnychronously", err);
                        response = ServiceResponse.error();
                    }
                    consumer.accept(response);
                };

                ui.access(() -> {
                    if (ui.isAttached())
                        uiTask.run();
                });
            });
    }

    public static <T extends Serializable> void callServicesAsync(
        List<? extends Supplier<? extends ServiceResponse<? extends T>>> callables,
        UI ui,
        SerializableConsumer<List<? extends ServiceResponse<? extends T>>> consumer) {

        List<? extends CompletableFuture<? extends ServiceResponse<? extends T>>> futures = callables.stream()
            // Dont replace this with a method reference, as it will fail in dev mode
            .map(supplier -> CompletableFuture.supplyAsync(supplier))
            .toList();

        CompletableFuture
            .allOf(futures.toArray(new CompletableFuture[0]))
            .whenComplete((v, err) -> {
                List<? extends ServiceResponse<? extends T>> res = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
                SerializableRunnable uiTask = () -> {
                    List<? extends ServiceResponse<? extends T>> response = res;
                    if (err != null) {
                        LOGGER.error("An error occurred while executing asynchronously", err);
                        response = List.of(ServiceResponse.error());
                    }
                    consumer.accept(response);
                };

                ui.access(() -> {
                    if (ui.isAttached())
                        uiTask.run();
                });
            });
    }
}
