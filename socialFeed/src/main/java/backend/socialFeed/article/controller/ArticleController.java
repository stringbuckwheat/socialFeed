package backend.socialFeed.article.controller;

import backend.socialFeed.article.dto.ArticleResponseDto;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}