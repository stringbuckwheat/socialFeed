package backend.socialFeed.article.repository;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query("SELECT a FROM Article a LEFT JOIN a.hashtags h " +
            "WHERE (:hashtag IS NULL OR h.name = :hashtag) " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND ((:searchBy = 'title' AND a.title LIKE %:search%) " +
            "OR (:searchBy = 'content' AND a.content LIKE %:search%) " +
            "OR (:searchBy = 'title,content' AND (a.title LIKE %:search% OR a.content LIKE %:search%)))")
    Page<Article> searchArticles(@Param("hashtag") String hashtag,
                                 @Param("type") ArticleType type,
                                 @Param("searchBy") String searchBy,
                                 @Param("search") String search,
                                 Pageable pageable);

}