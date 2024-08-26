package backend.socialFeed.article.entity;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.hashtags.entity.Hashtags;
import backend.socialFeed.user.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Article {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ArticleType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    // viewCount(조회수)의 디폴트 값은 0 이고(0부터 시작), null은 불가능하다.
    @Setter
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer shareCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "article")
    private List<Hashtags> hashtags;

    public void updateShareCount() {
        this.shareCount += 1;
    }

    public void updateLikeCount() {
        this.likeCount += 1;
    }
}