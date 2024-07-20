package dev.elfa.backend.model;

import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.personality.Personality;
import lombok.AllArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@AllArgsConstructor
@Document(collation = "influencer")
public class Influencer {
    @Id
    Long id;
    String name;
    Twitter twitter;
    Personality personality;
    Appearance appearance;
}
