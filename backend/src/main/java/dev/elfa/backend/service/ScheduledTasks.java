package dev.elfa.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final TwitterService twitterService;

    @Scheduled(fixedRate = 900000) // 15 minutes
    public void reportCurrentTime() {
        twitterService.postScheduledTweet();
    }
}
