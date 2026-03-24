package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatus;

import java.util.List;

public class SavedNotebook {

    private HasKernelData<?, ?, ?> kernel;
    private HasCompetitionData<?, ?, ?> competition;
    private ProcessingCompetition processingCompetition;
    private ProcessingStatus processingStatus;
    private String title;
    private List<SavedNotebook> children;

    private SavedNotebook(HasKernelData<?, ?, ?> kernel, HasCompetitionData<?, ?, ?> competition, List<SavedNotebook> children, String title) {
        this.kernel = kernel;
        this.competition = competition;
        this.children = children;
        this.title = title;
    }

    public SavedNotebook(HasKernelData<?, ?, ?> kernel) {
        this(kernel, null, null, null);
    }

    public SavedNotebook(HasCompetitionData<?, ?, ?> competition, List<SavedNotebook> children) {
        this(null, competition, children, null);
    }

    public SavedNotebook(List<SavedNotebook> children, String title) {
        this(null, null, children, title);
    }

    public SavedNotebook(ProcessingCompetition processingCompetition) {
        this.processingCompetition = processingCompetition;
    }

    public String getName() {
        if (kernel != null) {
            return kernel.getTitle();
        } else {
            return competition.getTitle();
        }
    }

    public HasKernelData<?, ?, ?> getKernel() {
        return kernel;
    }

    public HasCompetitionData<?, ?, ?> getCompetition() {
        return competition;
    }

    public String getNumNotebooks() {
        if (title != null) {
            return "";
        }
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

    public String getTitle() {
        return title;
    }

    public void setChildren(List<SavedNotebook> children) {
        this.children = children;
    }

    public void setCompetition(HasCompetitionData<?, ?, ?> competition) {
        this.competition = competition;
    }

    public void setKernel(HasKernelData<?, ?, ?> kernel) {
        this.kernel = kernel;
    }

    public void setProcessingCompetition(ProcessingCompetition processingCompetition) {
        this.processingCompetition = processingCompetition;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProcessingCompetition getProcessingCompetition() {
        return processingCompetition;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }
}
