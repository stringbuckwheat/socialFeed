package backend.socialFeed.article.service;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    // 게시글 id를 기준으로 게시글을 상세 조회하는 메서드
    public Article getArticle(String articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article with id " + articleId + " not found."));

        // repository 에서 찾은 article 의 viewCount 가 1 증가됨
        article.setViewCount(article.getViewCount() + 1);

        return articleRepository.save(article);
    }

    public Page<Article> getArticles(String hashtag, ArticleType type, String orderBy, String orderDirection, String searchBy, String search, int pageCount, int page) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderDirection), orderBy);
        Pageable pageable = PageRequest.of(page, pageCount, sort);
        return articleRepository.searchArticles(hashtag, type, searchBy, search, pageable);
    }
}