package backend.socialFeed.article.share.service;

import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import backend.socialFeed.article.share.constant.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ShareService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ShareService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void share(String articleId) {
        // 게시물 존재 여부
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalStateException(ErrorMessage.ARTICLE_NOT_EXISTS));

        // ShareCount 증가
        article.updateShareCount();
        articleRepository.save(article);
    }
}
