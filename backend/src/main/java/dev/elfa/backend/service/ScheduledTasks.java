package dev.elfa.backend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final TwitterService twitterService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
        String message = String.format("The time is now %s", dateFormat.format(new Date()));
        log.info(message);
//        TODO: twitterService.postScheduledTweet();
    }
}
