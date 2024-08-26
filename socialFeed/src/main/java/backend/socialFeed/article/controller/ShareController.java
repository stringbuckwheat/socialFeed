package backend.socialFeed.article.controller;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.share.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/article")
public class ShareController {

    private final ShareService shareService;

    @Autowired
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PatchMapping("{type}/share/{articleId}")
    public ResponseEntity<Void> shareArticle(@PathVariable ArticleType type, @PathVariable String articleId) {
        log.info("게시물 공유 요청 ID: {}", articleId);
        shareService.share(type, articleId);
        return ResponseEntity.noContent().build();
    }
}
