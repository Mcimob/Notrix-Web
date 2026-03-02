package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;

import java.io.Serial;

public abstract class AbstractKernelFilter<T extends HasKernelData<ID, ? extends HasCellData>, ID> extends AbstractFilter<T, ID>{
    @Serial
    private static final long serialVersionUID = 2094289064670774659L;

}
