package dev.elfa.backend.repository;

import dev.elfa.backend.model.Tweet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetsRepo extends MongoRepository<Tweet, String> {
    List<Tweet> findAllByApprovedEquals(boolean approved);
}
