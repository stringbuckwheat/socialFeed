package backend.socialFeed.article.entity;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
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

    // 조회수의 디폴트 값은 0 이고(0부터 시작), null은 불가능하다.
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

    public void plusView() {
        this.viewCount += 1;
    }
}
