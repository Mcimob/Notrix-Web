package ch.ethz.inf.peachlab.ui.provider;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.ClusterService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

public class ClusterProvider extends AbstractBackEndDataProvider<ClusterEntity, ClusterFilter> {

    private final ClusterService clusterService;

    public ClusterProvider() {
        this.clusterService = SpringContext.getBean(ClusterService.class);
    }

    @Override
    protected Stream<ClusterEntity> fetchFromBackEnd(Query<ClusterEntity, ClusterFilter> query) {
        ClusterFilter filter = query.getFilter().orElse(new ClusterFilter());
        ServiceResponse<PageImpl<ClusterEntity>> response = clusterService.fetch(PageRequest.of(
                        query.getOffset() / query.getLimit(),
                        query.getLimit(),
                        Sort.by(query.getSortOrders().stream()
                                .map(s -> new Sort.Order(getSortDirection(s), s.getSorted())).toList())),
                filter);
        return response.getEntity().map(PageImpl::stream).orElse(Stream.empty());
    }

    @Override
    protected int sizeInBackEnd(Query<ClusterEntity, ClusterFilter> query) {
        ClusterFilter filter = query.getFilter().orElse(new ClusterFilter());
        ServiceResponse<Long> countResponse = clusterService.count(filter);
        return Math.toIntExact(countResponse.getEntity().orElse(0L));
    }

    private static Sort.Direction getSortDirection(QuerySortOrder sort) {
        return switch (sort.getDirection()) {
            case ASCENDING -> Sort.Direction.ASC;
            case DESCENDING -> Sort.Direction.DESC;
        };
    }
}
