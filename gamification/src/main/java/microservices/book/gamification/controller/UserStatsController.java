package microservices.book.gamification.controller;

import lombok.RequiredArgsConstructor;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gamification 사용자 통계 서비스의 REST API
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class UserStatsController {

    private final GameService gameService;

    @GetMapping
    public GameStats getStatsForUser(@RequestParam("userId") Long userId) {
        return gameService.retrieveStatsForUser(userId);
    }
}
