package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class KernelEntity implements AbstractEntity {

    @Id
    @Column(nullable = false, name = "KernelVersionId")
    private Long id;

    @Column(nullable = false, name = "CreationDate")
    private LocalDateTime creationDate;

    @Column(nullable = true, name = "Title")
    private String title;

    @Column(nullable = false, name = "TotalVotes")
    private int totalVotes;

    @Column(nullable = false, name = "TotalViews")
    private int totalViews;

    @Column(nullable = false, name = "TotalComments")
    private int totalComments;

    @Column(nullable = true, name = "CurrentUrlSlug")
    private String currentUrlSlug;

    @Column(nullable = true, name = "AuthorUserName")
    private String authorUserName;

    @Column(nullable = true, name = "AuthorDisplayName")
    private String authorDisplayName;

    @OneToMany
    @JoinColumn(name = "KernelVersionId")
    private List<CellEntity> cells;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public String getCurrentUrlSlug() {
        return currentUrlSlug;
    }

    public void setCurrentUrlSlug(String currentUrlSlug) {
        this.currentUrlSlug = currentUrlSlug;
    }

    public String getAuthorUserName() {
        return authorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KernelEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return totalVotes == that.totalVotes
                && totalViews == that.totalViews
                && totalComments == that.totalComments
                && Objects.equals(id, that.id)
                && Objects.equals(creationDate, that.creationDate)
                && Objects.equals(title, that.title)
                && Objects.equals(currentUrlSlug, that.currentUrlSlug)
                && Objects.equals(authorUserName, that.authorUserName)
                && Objects.equals(authorDisplayName, that.authorDisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                id,
                creationDate,
                title,
                totalVotes,
                totalViews,
                totalComments,
                currentUrlSlug,
                authorUserName,
                authorDisplayName);
    }

    @Override
    public String toString() {
        return "KernelEntity{"
                + "id=" + id
                + ", creationDate=" + creationDate
                + ", title='" + title + '\''
                + ", totalVotes=" + totalVotes
                + ", totalViews=" + totalViews
                + ", totalComments=" + totalComments
                + ", currentUrlSlug='" + currentUrlSlug + '\''
                + ", authorUserName='" + authorUserName + '\''
                + ", authorDisplayName='" + authorDisplayName + '\''
                + "} " + super.toString();
    }
}
