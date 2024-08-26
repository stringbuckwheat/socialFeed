package backend.socialFeed.hashtags.entity;

import backend.socialFeed.article.entity.Article;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@NoArgsConstructor
@Entity
@Getter
@Builder
@AllArgsConstructor
@ToString(of = {"id", "name"})
public class Hashtags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @JsonBackReference
    @ManyToOne
    private Article article;

    public void setArticle(Article article) {
        this.article = article;
    }
}
