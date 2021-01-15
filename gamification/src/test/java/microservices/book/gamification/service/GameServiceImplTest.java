package microservices.book.gamification.service;

import microservices.book.gamification.client.MultiplicationResultAttemptClientImpl;
import microservices.book.gamification.domain.Badge;
import microservices.book.gamification.domain.BadgeCard;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.domain.ScoreCard;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GameServiceImplTest {

    private GameService gameService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;

    @Mock
    private MultiplicationResultAttemptClientImpl client;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameServiceImpl(scoreCardRepository, badgeCardRepository, client);
    }

    @Test
    public void 처음_정답을_맞추고_FIRST_WON_배지를_받는_경우() {
        //given
        Long userId = 1L;
        Long attemptId = 3L;
        int totalScore = 10;
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.save(any(ScoreCard.class))).willReturn(scoreCard);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(List.of(scoreCard));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Lists.emptyList());

        //when
        GameStats result = gameService.newAttemptForUser(userId, attemptId, true);

        //then
        assertThat(result.getBadges()).isEqualTo(List.of(Badge.FIRST_WON));
    }

    @Test
    public void 정답을_맞춰_브론즈_배지를_받는_경우() {
        //given
        Long userId = 1L;
        Long attemptId = 49L;
        int totalScore = 500;

        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.save(any(ScoreCard.class))).willReturn(scoreCard);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(Lists.emptyList());
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Lists.emptyList());

        //when
        GameStats result = gameService.newAttemptForUser(userId, attemptId, true);

        //then
        assertThat(result.getBadges()).contains(Badge.BRONZE);
    }
}
