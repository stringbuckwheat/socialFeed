package backend.socialFeed.article.like.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    public static final String ARTICLE_NOT_EXISTS = "게시물이 존재하지 않습니다.";
    public static final String TYPE_NOT_EXISTS = "타입이 존재하지 않습니다.";
}
