package dev.elfa.backend.repository;

import dev.elfa.backend.model.Influencer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerRepo extends MongoRepository<Influencer, String> {
}
