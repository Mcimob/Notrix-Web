package ch.ethz.inf.peachlab.ui.views;


import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public interface HasNotification {

    int DEFAULT_SHOW_TIME = 5_000;

    default void showInfoNotification(String message, Object... params) {
        setTextAndShow(
                new Notification("", DEFAULT_SHOW_TIME, Notification.Position.BOTTOM_START), message, params);
    }

    default void showWarningNotification(String message, Object... params) {
        Notification notification = new Notification("", DEFAULT_SHOW_TIME, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        setTextAndShow(notification, message, params);
    }

    default void showErrorNotification(String message, Object... params) {
        Notification notification = new Notification("", DEFAULT_SHOW_TIME, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        setTextAndShow(notification, message, params);
    }

    default void showSuccessNotification(String message, Object... params) {
        Notification notification = new Notification("", DEFAULT_SHOW_TIME, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setTextAndShow(notification, message, params);
    }

    default void showMessagesFromResponse(ServiceResponse<?> response) {
        response.getErrorMessages().forEach(this::showErrorNotification);
        response.getInfoMessages().forEach(this::showInfoNotification);
    }

    private void setTextAndShow(Notification notification, String message, Object... params) {
        notification.setText(notification.getTranslation(message, params));
        notification.open();
    }
}
