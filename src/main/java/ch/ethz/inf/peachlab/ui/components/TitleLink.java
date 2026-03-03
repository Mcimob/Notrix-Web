package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.ui.views.competition.AbstractCompetitionView;
import ch.ethz.inf.peachlab.ui.views.competition.CompetitionView;
import ch.ethz.inf.peachlab.ui.views.competition.UploadedCompetitionView;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.HasUrlParameter;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_LINK;

public class TitleLink extends Span {

    @Serial
    private static final long serialVersionUID = -5963398901254413082L;

    public <T, C extends Component & HasUrlParameter<T>> TitleLink(String title, Class<C> target, T urlParameter) {
        super(title);
        addClassNames(STYLE_TEXT_LINK);
        addClickListener(click ->
            UI.getCurrent().navigate(target, urlParameter));
    }

    public static TitleLink ofKernel(HasKernelData<?, ?, ?> kernel) {
        return new TitleLink(kernel.getTitle(), KernelView.class, kernel.getUrlParameter());
    }

    public static TitleLink ofCompetition(HasCompetitionData<?, ?, ?> competition) {
        if (competition instanceof CompetitionEntity) {
            return new TitleLink(competition.getTitle(), CompetitionView.class, competition.getUrlParameter());
        }
        return new TitleLink(competition.getTitle(), UploadedCompetitionView.class, competition.getUrlParameter());
    }


}
