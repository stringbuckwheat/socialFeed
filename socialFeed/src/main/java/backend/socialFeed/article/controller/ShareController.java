package backend.socialFeed.article.controller;

import backend.socialFeed.article.share.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/{type}/article")
public class ShareController {

    private final ShareService shareService;

    @Autowired
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PatchMapping("/share/{articleId}")
    public ResponseEntity<Void> shareArticle(@PathVariable String articleId) {
        log.info("게시물 공유 요청 with ID: {}", articleId);
        shareService.share(articleId);
        return ResponseEntity.noContent().build();
    }
}
