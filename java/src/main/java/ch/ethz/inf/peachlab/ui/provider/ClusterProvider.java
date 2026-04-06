package ch.ethz.inf.peachlab.ui.provider;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.BaseService;
import ch.ethz.inf.peachlab.model.entity.HasBaseStats;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.filter.AbstractClusterFilter;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.function.Function;
import java.util.stream.Stream;

public class ClusterProvider<C extends HasClusterData<?, ?>, CF extends AbstractClusterFilter<C, ?, ?>> extends AbstractBackEndHierarchicalDataProvider<HasBaseStats, CF> {

    @Serial
    private static final long serialVersionUID = 2812842525037289551L;
    private final BaseService<C, CF, Long> service;

    public ClusterProvider(BaseService<C, CF, Long> service) {
        this.service = service;
    }

    protected Stream<C> fetchFromBackEnd(Query<HasBaseStats, CF> query) {
        CF filter = query.getFilter().orElseThrow();
        ServiceResponse<PageImpl<C>> response = service.fetch(PageRequest.of(
                query.getOffset() / query.getLimit(),
                query.getLimit(),
                Sort.by(Sort.Direction.ASC, "localClusterId")),
            filter);
        return response.getEntity().map(PageImpl::stream).orElse(Stream.empty());
    }

    protected int sizeInBackEnd(Query<HasBaseStats, CF> query) {
        CF filter = query.getFilter().orElseThrow();
        ServiceResponse<Long> countResponse = service.count(filter);
        return Math.toIntExact(countResponse.getEntity().orElse(0L));
    }

    @Override
    protected Stream<HasBaseStats> fetchChildrenFromBackEnd(HierarchicalQuery<HasBaseStats, CF> query) {
        if (query.getParent() != null) {
            return query.getParent().getChildren().stream();
        }
        return fetchFromBackEnd(query)
            .map(Function.identity());
    }

    @Override
    public int getChildCount(HierarchicalQuery<HasBaseStats, CF> query) {
        if (query.getParent() != null) {
            return query.getParent().getChildren().size();
        }
        return sizeInBackEnd(query);
    }

    @Override
    public boolean hasChildren(HasBaseStats item) {
        return !item.getChildren().isEmpty();
    }
}
