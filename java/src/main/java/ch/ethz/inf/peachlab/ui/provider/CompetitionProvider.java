package ch.ethz.inf.peachlab.ui.provider;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.stream.Stream;

public class CompetitionProvider extends AbstractBackEndDataProvider<CompetitionEntity, CompetitionFilter> {

    @Serial
    private static final long serialVersionUID = -2095462497869355272L;
    private final CompetitionService competitionService;

    public CompetitionProvider() {
        this.competitionService = SpringContext.getBean(CompetitionService.class);
    }

    @Override
    protected Stream<CompetitionEntity> fetchFromBackEnd(Query<CompetitionEntity, CompetitionFilter> query) {
        CompetitionFilter filter = query.getFilter().orElse(new CompetitionFilter());
        ServiceResponse<PageImpl<CompetitionEntity>> response = competitionService.fetch(PageRequest.of(
                        query.getOffset() / query.getLimit(),
                        query.getLimit(),
                        Sort.by(query.getSortOrders().stream()
                                .map(s -> new Sort.Order(getSortDirection(s), s.getSorted())).toList())),
                filter);
        return response.getEntity().map(PageImpl::stream).orElse(Stream.empty());
    }

    @Override
    protected int sizeInBackEnd(Query<CompetitionEntity, CompetitionFilter> query) {
        CompetitionFilter filter = query.getFilter().orElse(new CompetitionFilter());
        ServiceResponse<Long> countResponse = competitionService.count(filter);
        return Math.toIntExact(countResponse.getEntity().orElse(0L));
    }

    private static Sort.Direction getSortDirection(QuerySortOrder sort) {
        return switch (sort.getDirection()) {
            case ASCENDING -> Sort.Direction.ASC;
            case DESCENDING -> Sort.Direction.DESC;
        };
    }
}
