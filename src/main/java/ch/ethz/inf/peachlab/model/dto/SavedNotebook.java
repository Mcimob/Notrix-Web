package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;

import java.util.List;

public class SavedNotebook {

    private final HasKernelData<?, ?> kernel;
    private final CompetitionEntity competition;
    private final List<SavedNotebook> children;

    private SavedNotebook(HasKernelData<?, ?> kernel, CompetitionEntity competition, List<SavedNotebook> children) {
        this.kernel = kernel;
        this.competition = competition;
        this.children = children;
    }

    public SavedNotebook(HasKernelData<?, ?> kernel) {
        this(kernel, null, null);
    }

    public SavedNotebook(CompetitionEntity competition, List<SavedNotebook> children) {
        this(null, competition, children);
    }

    public String getName() {
        if (kernel != null) {
            return kernel.getTitle();
        } else {
            return competition.getTitle();
        }
    }

    public HasKernelData<?, ?> getKernel() {
        return kernel;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    public String getNumNotebooks() {
        if (children != null) {
            return String.valueOf(children.size());
        }
        return "";
    }

    public List<SavedNotebook> getChildren() {
        if (children == null) {
            return List.of();
        }
        return children;
    }
}
