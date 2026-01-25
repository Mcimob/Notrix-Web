package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

@Route(value = "code", layout = MainLayout.class)
public class KernelView extends AbstractView implements HasUrlParameter<String> {

    private final KernelService kernelService;

    private KernelEntity kernel;

    public KernelView(KernelService kernelService) {
        this.kernelService = kernelService;
    }

    @Override
    public void render() {

    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter.isEmpty()) {
            add(new Text("Invalid!"));
            return;
        }
        String[] parts = parameter.split("/");
        if (parts.length < 2) {
            add(new Text("Invalid!"));
            return;
        }
        String user = parts[0];
        String slug = parts[1];
        KernelFilter filter = new KernelFilter();
        filter.setUser(user);
        filter.setSlug(slug);

        ServiceResponse<KernelEntity> response = kernelService.fetchOne(filter, KernelLoadType.WITH_CELLS);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            UI.getCurrent().getPage().getHistory().back();
            return;
        }

        kernel = response.getEntity().get();
    }
}
