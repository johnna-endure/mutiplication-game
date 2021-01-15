package microservices.book.gamification.repository;

import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.domain.ScoreCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ScoreCard CRUD 작업 처리
 */
public interface ScoreCardRepository extends CrudRepository<ScoreCard, Long> {
    /**
     * ScoreCard의 점수를 합해서 사용자의 총 점수를 조회
     * @param userId 총 점수를 조회하고자 하는 사용자의 Id
     * @return 사용자의 총 점수
     */
    @Query("select sum(s.score) from microservices.book.gamification.domain.ScoreCard s " +
        "where s.userId = :userId group by s.userId")
    int getTotalScoreForUser(@Param("userId") Long userId);

    /**
     * 사용자와 사용자의 총 점수를 나타내는 {@link LeaderBoardRow} 리스트를 조회
     * @return 높은 점수순으로 정렬된 리더보드
     */
    @Query("select new microservices.book.gamification.domain.LeaderBoardRow(s.userId, sum(s.score)) " +
        "from microservices.book.gamification.domain.ScoreCard s " +
        "group by s.userId order by sum(s.score) desc")
    List<LeaderBoardRow> findFirst10();

    /**
     * 사용자의 모든 ScoreCard를 조회
     * @param userId 사용자 ID
     * @return 특정 사용자의 최근순으로 정렬된 ScoreCard 리스트
     */
    List<ScoreCard> findByUserIdOrderByScoreTimestampDesc(Long userId);
}
