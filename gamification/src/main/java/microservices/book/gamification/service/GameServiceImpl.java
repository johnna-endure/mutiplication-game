package microservices.book.gamification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.book.gamification.client.MultiplicationResultAttemptClient;
import microservices.book.gamification.client.dto.MultiplicationResultAttempt;
import microservices.book.gamification.domain.Badge;
import microservices.book.gamification.domain.BadgeCard;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.domain.ScoreCard;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class GameServiceImpl implements GameService{

    public static final int LUCKY_NUMBER = 42;

    private final ScoreCardRepository scoreCardRepository;
    private final BadgeCardRepository badgeCardRepository;
    private final MultiplicationResultAttemptClient attemptClient;

    @Override
    public GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct) {
        //처음엔 답이 맞았을 때만 점수를 줌
        if(correct) {
            ScoreCard scoreCard = new ScoreCard(userId, attemptId);
            scoreCard = scoreCardRepository.save(scoreCard);
            log.info("사용자 ID {}, 점수 {} 점, 답안 ID {}", userId, scoreCard, attemptId);
            List<BadgeCard> badgeCards = processForBadges(userId, attemptId);
            return new GameStats(userId, scoreCard.getScore(),
                badgeCards.stream()
                    .map(BadgeCard::getBadge)
                    .collect(Collectors.toList()));
        }
        return GameStats.emptyStats(userId);
    }

    /**
     * 조건이 충족될 경우 새 배지를 제공하기 위해 얻은 총 점수와 점수 카드를 확인
     */
    private List<BadgeCard> processForBadges(Long userId, Long attemptId) {
        List<BadgeCard> badgeCards = new ArrayList<>();

        int totalScore = scoreCardRepository.getTotalScoreForUser(userId);
        log.info("사용자 ID {} 의 새로운 점수 {}",userId, totalScore);


        List<ScoreCard> scoreCardList = scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId);
        List<BadgeCard> badgeCardList = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);

        //점수 기반 배지
        checkAndGivenBadgeBasedOnScore(badgeCardList, Badge.BRONZE, totalScore, 100, userId)
            .ifPresent(badgeCards::add);
        checkAndGivenBadgeBasedOnScore(badgeCardList, Badge.SILVER, totalScore, 500, userId);
        checkAndGivenBadgeBasedOnScore(badgeCardList, Badge.GOLD, totalScore, 999, userId);

        //첫번째 정답 배지
        if(scoreCardList.size() == 1 && !containsBadge(badgeCardList, Badge.FIRST_WON)) {
            BadgeCard firstWonBadge = giveBadgeToUser(Badge.FIRST_WON, userId);
            badgeCards.add(firstWonBadge);
        }

        //행운의 숫자 배지
        MultiplicationResultAttempt attempt = attemptClient.retrieveMultiplicationResultAttemptById(attemptId);
        boolean hasLuckNumber = (LUCKY_NUMBER == attempt.getMultiplicationFactorA() || LUCKY_NUMBER == attempt.getMultiplicationFactorA());
        if(!containsBadge(badgeCardList, Badge.LUCKY_NUMBER) && hasLuckNumber) {
            BadgeCard luckyNumberBadge = giveBadgeToUser(Badge.LUCKY_NUMBER, userId);
            badgeCards.add(luckyNumberBadge);
        }

        return badgeCards;
    }

    @Override
    public GameStats retrieveStatsForUser(Long userId) {
        Integer score = scoreCardRepository.getTotalScoreForUser(userId);
        List<BadgeCard> badgeCards = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);
        return new GameStats(userId, score,
            badgeCards.stream()
                .map(BadgeCard::getBadge)
                .collect(Collectors.toList()));
    }

    /**
     * 배지를 얻기 위한 조건을 넘는지 체크하는 편의성 메서드
     * 또한 조건이 충족되면 사용자에게 배지를 부여여
     */
    private Optional<BadgeCard> checkAndGivenBadgeBasedOnScore(List<BadgeCard> badgeCards, Badge badge,
                                                               int score, int scoreThreshold, Long userId) {
        if(score >= scoreThreshold && !containsBadge(badgeCards, badge)) {
            return Optional.of(giveBadgeToUser(badge, userId));
        }
        return Optional.empty();
    }

    /**
     * 배지 목록에 배지가 포함돼 있는지 확인하는 메서드
     */
    private boolean containsBadge(List<BadgeCard> badgeCards, Badge badge) {
        return badgeCards.stream().anyMatch(b -> b.getBadge().equals(badge));
    }

    /**
     * 주어진 사용자에게 새로운 배지를 부여하는 메서드
     */
    private BadgeCard giveBadgeToUser(Badge badge, Long userId) {
        BadgeCard badgeCard = new BadgeCard(userId, badge);
        badgeCardRepository.save(badgeCard);
        log.info("사용자 ID {}, 새로운 배지 획득: {}", userId, badge);
        return badgeCard;
    }

}
