package microservices.book.gamification.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 리더보드 내 위치를 나타내는 객체
 * 사용자와 전제 첨수를 연결
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LeaderBoardRow {

    private final Long userId;
    private final Long totalScore;

    // JSON/JPA 용 빈 생성자
    protected LeaderBoardRow() {
        this(null, null);
    }
}
