package microservices.book.gamification.service;

import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.repository.ScoreCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

public class LeaderBoardServiceImplTest {

    private LeaderBoardServiceImpl leaderBoardService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        leaderBoardService = new LeaderBoardServiceImpl(scoreCardRepository);
    }

    @Test
    public void getCurrentLeaderBoard_기본동작테스트() {
        //given
        List<LeaderBoardRow> leaderBoardRows = LongStream.range(0, 10)
            .mapToObj(l -> new LeaderBoardRow(l, 100L - 10L*l))
            .collect(Collectors.toList());
        given(scoreCardRepository.findFirst10()).willReturn(leaderBoardRows);

        //when
        List<LeaderBoardRow> result = leaderBoardService.getCurrentLeaderBoard();

        //then
        assertThat(result).isEqualTo(leaderBoardRows);
    }
}
