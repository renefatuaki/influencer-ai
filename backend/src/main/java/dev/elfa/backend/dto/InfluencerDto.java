package dev.elfa.backend.dto;

import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Scheduler;
import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.personality.Personality;

public record InfluencerDto(
        String id,
        TwitterDto twitter,
        Personality personality,
        Appearance appearance,
        Scheduler scheduler
) {
    public static InfluencerDto convertToDto(Influencer influencer) {
        TwitterDto twitterDto = new TwitterDto(
                influencer.getTwitter().id(),
                influencer.getTwitter().name(),
                influencer.getTwitter().username(),
                new AuthDto(influencer.getTwitter().auth().isAuthorized())
        );

        return new InfluencerDto(influencer.getId(), twitterDto, influencer.getPersonality(), influencer.getAppearance(), influencer.getScheduler());
    }
}
