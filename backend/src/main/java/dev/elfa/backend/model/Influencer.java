package dev.elfa.backend.model;

import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.personality.Personality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "influencer")
public class Influencer {
    @Id
    private String id;
    private Twitter twitter;
    private Personality personality;
    private Appearance appearance;

    public Influencer(Twitter twitter) {
        this.twitter = twitter;
    }
}
