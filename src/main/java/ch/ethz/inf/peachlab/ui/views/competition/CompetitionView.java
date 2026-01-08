package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "competitions", layout = MainLayout.class)
public class CompetitionView extends AbstractView implements HasUrlParameter<String> {

    private final transient CompetitionService competitionService;

    private CompetitionEntity competition;

    public CompetitionView(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @Override
    public void render() {
        add(new Text(competition.getTitle()));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String slug) {
        CompetitionFilter filter = new CompetitionFilter();
        filter.setSlug(slug);
        ServiceResponse<CompetitionEntity> response = competitionService.fetchOne(filter);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            return;
        }
        competition = response.getEntity().get();
    }
}
