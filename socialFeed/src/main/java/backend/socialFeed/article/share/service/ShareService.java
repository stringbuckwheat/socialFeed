package backend.socialFeed.article.share.service;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.respotiory.ArticleRepository;
import backend.socialFeed.article.share.constant.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Map;
import java.util.EnumMap;

@Slf4j
@Service
public class ShareService {

    private final ArticleRepository articleRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ShareService(ArticleRepository articleRepository, WebClient.Builder webClientBuilder) {
        this.articleRepository = articleRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Transactional
    public void share(ArticleType type, String articleId) {
        // 게시물 존재 여부
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalStateException(ErrorMessage.ARTICLE_NOT_EXISTS));

        String shareUrl = getURL(type, articleId);
        log.info("게시물 공유 요청 API: {}", shareUrl);

        // WebClient 생성
        WebClient webClient = webClientBuilder
                .baseUrl(shareUrl)
                .build();

        // 공유 API 호출
        String response = webClient.post()
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        ClientResponse::createException
                )
                .bodyToMono(String.class)
                .block();

        //
        log.info("API 호출 성공. 응답: {}", response);

        // ShareCount 증가
        article.updateShareCount();
        articleRepository.save(article);
    }

    private String getURL(ArticleType type, String articleId){
        log.info("URL 생성 Type: {}", type);
        // Type에 따른 URL 생성
        Map<ArticleType, String> urlType = new EnumMap<>(ArticleType.class);
        urlType.put(ArticleType.FACEBOOK, "https://www.facebook.com/share/");
        urlType.put(ArticleType.TWITTER, "https://www.twitter.com/share/");
        urlType.put(ArticleType.INSTAGRAM, "https://www.instagram.com/share/");
        urlType.put(ArticleType.THREADS, "https://www.threads.net/share/");

        String baseUrl = urlType.get(type);

        if (baseUrl == null) {
            throw new IllegalStateException(ErrorMessage.TYPE_NOT_EXISTS);
        }

        return baseUrl + articleId;
    }
}
