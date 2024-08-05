package dev.elfa.backend.repository;

import dev.elfa.backend.model.Tweet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetsRepo extends MongoRepository<Tweet, String> {
}
