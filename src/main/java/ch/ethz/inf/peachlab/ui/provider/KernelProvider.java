package ch.ethz.inf.peachlab.ui.provider;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

public class KernelProvider extends AbstractBackEndDataProvider<KernelEntity, KernelFilter> {

    private final KernelService competitionService;

    public KernelProvider() {
        this.competitionService = SpringContext.getBean(KernelService.class);
    }

    @Override
    protected Stream<KernelEntity> fetchFromBackEnd(Query<KernelEntity, KernelFilter> query) {
        KernelFilter filter = query.getFilter().orElse(new KernelFilter());
        ServiceResponse<Page<KernelEntity>> response = competitionService.fetch(PageRequest.of(
                        query.getOffset() / query.getLimit(),
                        query.getLimit(),
                        Sort.by(query.getSortOrders().stream()
                                .map(s -> new Sort.Order(getSortDirection(s), s.getSorted())).toList())),
                filter, KernelLoadType.WITH_CELLS);
        return response.getEntity().orElse(Page.empty()).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<KernelEntity, KernelFilter> query) {
        KernelFilter filter = query.getFilter().orElse(new KernelFilter());
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
