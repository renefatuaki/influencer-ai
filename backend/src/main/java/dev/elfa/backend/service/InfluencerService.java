package dev.elfa.backend.service;

import dev.elfa.backend.dto.AuthDto;
import dev.elfa.backend.dto.InfluencerDto;
import dev.elfa.backend.dto.TwitterDto;
import dev.elfa.backend.model.FileMetadata;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Scheduler;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.image.Image;
import dev.elfa.backend.model.personality.Personality;
import dev.elfa.backend.repository.InfluencerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfluencerService {
    private final InfluencerRepo influencerRepo;

    public Optional<InfluencerDto> getInfluencerDto(String id) {
        Optional<Influencer> influencer = influencerRepo.findById(id);

        return influencer.map(this::mapToInfluencerResponseDto);
    }

    public Page<InfluencerDto> getInfluencers(Pageable pageable) {
        Page<Influencer> influencerPage = influencerRepo.findAll(pageable);

        return influencerPage.map(this::mapToInfluencerResponseDto);
    }

    private InfluencerDto mapToInfluencerResponseDto(Influencer influencer) {
        Twitter twitter = influencer.getTwitter();
        AuthDto authDto = new AuthDto(twitter.auth().isAuthorized());
        TwitterDto twitterDto = new TwitterDto(twitter.id(), twitter.name(), twitter.username(), authDto);

        return new InfluencerDto(influencer.getId(), twitterDto, influencer.getPersonality(), influencer.getAppearance(), influencer.getScheduler());
    }

    public Optional<Influencer> getInfluencer(String id) {
        return influencerRepo.findById(id);
    }

    public boolean deleteInfluencer(String id) {
        if (influencerRepo.existsById(id)) {
            influencerRepo.deleteById(id);
            return true;
        }
        
        return false;
    }

    @Transactional
    public Optional<Personality> updatePersonality(String id, Personality personality) {
        return influencerRepo.findById(id).map(influencer -> {
            influencer.setPersonality(personality);
            influencerRepo.save(influencer);

            return influencer.getPersonality();
        });
    }

    @Transactional
    public Optional<Appearance> updateAppearance(String id, Appearance appearance) {
        return influencerRepo.findById(id).map(influencer -> {
            influencer.setAppearance(appearance);
            influencerRepo.save(influencer);

            return influencer.getAppearance();
        });
    }

    @Transactional
    public void saveBaseImage(FileMetadata fileMetadata) {
        influencerRepo.findById(fileMetadata.getAccountId()).ifPresent(influencer -> {
            Image image = influencer.getImage().withBaseImage(fileMetadata.getId());
            influencer.setImage(image);
            influencerRepo.save(influencer);
        });
    }

    @Transactional
    public Optional<Scheduler> updateScheduler(String id, Scheduler scheduler) {
        return influencerRepo.findById(id).map(influencer -> {
            influencer.setScheduler(scheduler);
            influencerRepo.save(influencer);

            return influencer.getScheduler();
        });
    }
}
