package microservices.book.gamification.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import microservices.book.gamification.client.MultiplicationResultAttemptDeserializer;

/**
 * User가 곱셈을 푼 답안을 정의한 클래스
 */
@RequiredArgsConstructor
@Getter
@ToString
@JsonDeserialize(using = MultiplicationResultAttemptDeserializer.class)
public class MultiplicationResultAttempt {

    private final String userAlias;
    private final int multiplicationFactorA;
    private final int multiplicationFactorB;
    private final int resultAttempt;

    private final boolean correct;

    // JSON/JPA 를 위한 빈 생성자
    protected MultiplicationResultAttempt() {
        this(null, -1, -1, -1,false);
    }

}
