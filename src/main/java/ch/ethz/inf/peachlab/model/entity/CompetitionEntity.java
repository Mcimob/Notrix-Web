package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.backend.dao.csv.converter.LocalDateTimeConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class CompetitionEntity extends AbstractEntity {

    @Id
    @CsvBindByName
    private Long id;

    @Column(nullable = false)
    @CsvBindByName
    private String title;

    @Column(nullable = false)
    @CsvBindByName
    private String subTitle;

    @Column(nullable = false)
    @CsvBindByName
    private String slug;

    @Column(nullable = false)
    @CsvBindByName
    private Long totalSubmissions;

    @Column(nullable = false)
    @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
    private LocalDateTime deadlineDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String description) {
        this.subTitle = description;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "CompetitionEntity{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + subTitle + '\''
                + ", slug='" + slug + '\''
                + ", totalSubmissions=" + totalSubmissions
                + ", deadlineDate=" + deadlineDate
                + "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CompetitionEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(subTitle, that.subTitle)
                && Objects.equals(slug, that.slug)
                && Objects.equals(totalSubmissions, that.totalSubmissions)
                && Objects.equals(deadlineDate, that.deadlineDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                id,
                title,
                subTitle,
                slug,
                totalSubmissions,
                deadlineDate);
    }
}
