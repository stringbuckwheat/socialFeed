package backend.socialFeed.article.controller;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.like.service.LikeService;
import backend.socialFeed.article.share.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/article")
public class LikeController {
    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PatchMapping("{type}/like/{articleId}")
    public ResponseEntity<Void> shareArticle(@PathVariable ArticleType type, @PathVariable String articleId) {
        log.info("게시물 공유 요청 ID: {}", articleId);
        likeService.like(type, articleId);
        return ResponseEntity.noContent().build();
    }
}
