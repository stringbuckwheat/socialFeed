package backend.socialFeed.article.controller;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.dto.ArticleResponseDto;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.IntSet;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/api/article/{articleId}")
    public ResponseEntity<?> getArticle(
            @PathVariable("articleId") String articleId) {
        Article article = articleService.getArticle(articleId);

        // 엔티티를 response DTO 로 변환
        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .id(article.getId())
                .userId(article.getUser().getId())
                .type(article.getType())
                .title(article.getTitle())
                .content(article.getContent())
                .viewCount(article.getViewCount())
                .likeCount(article.getLikeCount())
                .shareCount(article.getShareCount())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();

        // DTO 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/api/article/")
    public List<Article> getArticles(
            @RequestParam(required = false) String hashtag,
            @RequestParam(required = false) ArticleType type,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection,
            @RequestParam(defaultValue = "title,content") String searchBy,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "10") int pageCount,
            @RequestParam(defaultValue = "0") int page) {
        Page<Article> articlesPage = articleService.getArticles(
                hashtag, type, orderBy, orderDirection, searchBy, search, pageCount, page
        );

        return articlesPage.stream()
                .map(article -> {
                    // 최대 20자만 포함
                    article.setContent(article.getContent().substring(0, Math.min(article.getContent().length(), 20)));
                    return article;
                })
                .collect(Collectors.toList());
    }
    

}