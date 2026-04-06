package ch.ethz.inf.peachlab.ui.provider;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.BaseService;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.filter.AbstractKernelFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class KernelProvider<K extends HasKernelData<?, ?, ?>, KF extends AbstractKernelFilter<K, ?, ?>> extends AbstractBackEndDataProvider<HasKernelData<?, ?, ?>, AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?>> {

    private final BaseService<K, KF, ?> service;
    private final List<HasKernelData<?, ?, ?>> localKernels;

    public KernelProvider(BaseService<K, KF, ?> service, List<HasKernelData<?, ?, ?>> localKernels) {
        this.service = service;
        this.localKernels = localKernels;
    }

    @Override
    protected Stream<HasKernelData<?, ?, ?>> fetchFromBackEnd(Query<HasKernelData<?, ?, ?>, AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?>> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();
        int numFullLocalPages = localKernels.size() / limit;

        int requestedPage = offset / limit;
        if (requestedPage < numFullLocalPages) {
            return localKernels.stream().skip((long) requestedPage * limit).limit(limit);
        }

        Sort sort = Sort.by(Optional.ofNullable(query.getSortOrders()).orElse(List.of()).stream()
            .map(s -> new Sort.Order(getSortDirection(s), s.getSorted())).toList());
        AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?> filter = query.getFilter().orElseThrow();

        if (requestedPage == numFullLocalPages) {
            Stream<HasKernelData<?, ?, ?>> firstPart = localKernels.stream().skip((long) numFullLocalPages * limit);
            PageRequest page = PageRequest.of(0, limit, sort);
            //noinspection unchecked
            return Stream.concat(firstPart,
                    service.fetch(page, (KF) filter).getEntity()
                    .map(PageImpl::stream)
                    .orElse(Stream.empty()))
                .limit(limit);

        }
        int localOverflow = localKernels.size() % limit;
        int basePageNUmber = requestedPage - numFullLocalPages;
        PageRequest page1 = PageRequest.of(basePageNUmber - 1, limit, sort);
        PageRequest page2 = PageRequest.of(basePageNUmber, limit, sort);
        //noinspection unchecked
        return Stream.of(page1, page2)
            .map(p -> service.fetch(p, (KF) filter))
            .map(ServiceResponse::getEntity)
            .flatMap(Optional::stream)
            .flatMap(PageImpl::stream)
            .skip(limit - localOverflow)
            .limit(limit)
            .map(e -> (HasKernelData<?, ?, ?>) e);
    }

    @Override
    protected int sizeInBackEnd(Query<HasKernelData<?, ?, ?>, AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?>> query) {
        AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?> filter = query.getFilter().orElseThrow();
        //noinspection unchecked
        return localKernels.size() + service.count((KF) filter).getEntity().orElse(0L).intValue();
    }

    private static Sort.Direction getSortDirection(QuerySortOrder sort) {
        return switch (sort.getDirection()) {
            case ASCENDING -> Sort.Direction.ASC;
            case DESCENDING -> Sort.Direction.DESC;
        };
    }
}
