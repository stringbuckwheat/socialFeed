package backend.socialFeed.article.like.service;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import backend.socialFeed.article.like.constant.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Service
public class LikeService {
    private final ArticleRepository articleRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public LikeService(ArticleRepository articleRepository, WebClient.Builder webClientBuilder) {
        this.articleRepository = articleRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Transactional
    public void like(ArticleType type, String articleId) {
        // 게시물 존재 여부
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.ARTICLE_NOT_EXISTS));

        String likeUrl = getURL(type, articleId);
        log.info("게시물 좋아요 요청 API: {}", likeUrl);

        // WebClient 생성
        WebClient webClient = webClientBuilder
                .baseUrl(likeUrl)
                .build();

        // 좋아요 API 호출
        Flux<String> response = webClient.post()
                .retrieve()
                .bodyToFlux(String.class);

        response.subscribe(
                data -> log.info("API 호출 성공 결과: {}", data),
                error -> log.error("API 호출 실패 결과: {}", error.getMessage()),
                () -> log.info("API 호출이 완료되었습니다.")
        );


        // LikeCount 증가
        article.updateLikeCount();
        articleRepository.save(article);
    }

    private String getURL(ArticleType type, String articleId){
        log.info("URL 생성 Type: {}", type);
        // Type에 따른 URL 생성
        Map<ArticleType, String> urlType = new EnumMap<>(ArticleType.class);
        urlType.put(ArticleType.FACEBOOK, "https://www.facebook.com/likes/");
        urlType.put(ArticleType.TWITTER, "https://www.twitter.com/likes/");
        urlType.put(ArticleType.INSTAGRAM, "https://www.instagram.com/likes/");
        urlType.put(ArticleType.THREADS, "https://www.threads.net/likes/");

        String baseUrl = urlType.get(type);

        if (baseUrl == null) {
            throw new IllegalStateException(ErrorMessage.TYPE_NOT_EXISTS);
        }

        return baseUrl + articleId;
    }
}