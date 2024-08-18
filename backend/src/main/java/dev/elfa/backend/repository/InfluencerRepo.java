package dev.elfa.backend.repository;

import dev.elfa.backend.model.Influencer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface InfluencerRepo extends MongoRepository<Influencer, String> {
    List<Influencer> findBySchedulerScheduledTimeBetween(LocalTime time, LocalTime time2);
}
