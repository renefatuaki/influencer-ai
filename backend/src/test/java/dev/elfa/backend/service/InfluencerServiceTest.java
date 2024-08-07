package dev.elfa.backend.service;

import dev.elfa.backend.dto.AuthDto;
import dev.elfa.backend.dto.InfluencerDto;
import dev.elfa.backend.dto.TwitterDto;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.appearance.*;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.model.personality.Interest;
import dev.elfa.backend.model.personality.Personality;
import dev.elfa.backend.model.personality.Tone;
import dev.elfa.backend.repository.InfluencerRepo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class InfluencerServiceTest {
    private final InfluencerRepo mockInfluencerRepo = mock(InfluencerRepo.class);

    @Test
    void getInfluencerDto_ValidId_ReturnsInfluencerResponseDto() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Auth auth = new Auth(true, "token", "secret", currentDateTime);
        Twitter twitter = new Twitter("1", "name", "username", auth);
        Influencer influencer = new Influencer("1", twitter, null, null);
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.of(influencer));

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        AuthDto authDto = new AuthDto(true);
        TwitterDto twitterDto = new TwitterDto("1", "name", "username", authDto);
        InfluencerDto influencerDto = new InfluencerDto("1", twitterDto, null, null);

        Optional<InfluencerDto> actualResponseDto = influencerService.getInfluencerDto("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualResponseDto, Optional.of(influencerDto));
    }

    @Test
    void getInfluencerDto_IdNotFound_ReturnsEmpty() {
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.empty());

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<InfluencerDto> actualResponseDto = influencerService.getInfluencerDto("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertTrue(actualResponseDto.isEmpty());
    }

    @Test
    void getInfluencerDto_EmptyId_ReturnsEmpty() {
        when(mockInfluencerRepo.findById("")).thenReturn(Optional.empty());

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<InfluencerDto> actualResponseDto = influencerService.getInfluencerDto("");
        verify(mockInfluencerRepo, times(1)).findById("");
        assertEquals(actualResponseDto, Optional.empty());
    }

    @Test
    void getInfluencers_ValidPageable_ReturnsInfluencerResponseDtoPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

        Page<Influencer> influencerPage = getInfluencersPageData();

        when(mockInfluencerRepo.findAll(pageable)).thenReturn(influencerPage);

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Page<InfluencerDto> influencerResponseDtoPage = getInfluencersDtoPageData();

        Page<InfluencerDto> actualResponseDto = influencerService.getInfluencers(pageable);
        verify(mockInfluencerRepo, times(1)).findAll(pageable);
        assertEquals(actualResponseDto, influencerResponseDtoPage);
    }

    private static @NotNull Page<Influencer> getInfluencersPageData() {
        Auth auth1 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter1 = new Twitter("1", "name", "username", auth1);
        Influencer influencer1 = new Influencer("1", twitter1, null, null);

        Auth auth2 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter2 = new Twitter("1", "name", "username", auth2);
        Influencer influencer2 = new Influencer("1", twitter2, null, null);

        return new PageImpl<>(Arrays.asList(influencer1, influencer2));
    }


    private static @NotNull Page<InfluencerDto> getInfluencersDtoPageData() {
        AuthDto authDto1 = new AuthDto(true);
        TwitterDto twitterDto1 = new TwitterDto("1", "name", "username", authDto1);
        InfluencerDto influencerDto1 = new InfluencerDto("1", twitterDto1, null, null);

        AuthDto authDto2 = new AuthDto(true);
        TwitterDto twitterDto2 = new TwitterDto("1", "name", "username", authDto2);
        InfluencerDto influencerDto2 = new InfluencerDto("1", twitterDto2, null, null);

        return new PageImpl<>(Arrays.asList(influencerDto1, influencerDto2));
    }

    @Test
    void getInfluencers_EmptyList_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

        when(mockInfluencerRepo.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Page<InfluencerDto> actualResponseDto = influencerService.getInfluencers(pageable);
        verify(mockInfluencerRepo, times(1)).findAll(pageable);
        assertEquals(actualResponseDto, new PageImpl<>(Collections.emptyList()));
    }

    @Test
    void getInfluencer_ValidId_ReturnsInfluencer() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Auth auth = new Auth(true, "token", "secret", currentDateTime);
        Twitter twitter = new Twitter("1", "name", "username", auth);
        Influencer influencer = new Influencer("1", twitter, null, null);
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.of(influencer));

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Influencer> actualInfluencer = influencerService.getInfluencer("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualInfluencer, Optional.of(influencer));
    }

    @Test
    void getInfluencer_IdNotFound_ReturnsEmpty() {
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.empty());

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Influencer> actualInfluencer = influencerService.getInfluencer("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualInfluencer, Optional.empty());
    }

    @Test
    void deleteInfluencer_ValidId_DeletesInfluencer() {
        doNothing().when(mockInfluencerRepo).deleteById("1");

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        boolean isDeleted = influencerService.deleteInfluencer("1");
        verify(mockInfluencerRepo, times(1)).deleteById("1");
        assertTrue(isDeleted);
    }

    @Test
    void updatePersonality_ValidRequest_ReturnsAcceptedStatus() {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        Personality personalityToUpdate = new Personality(Set.of(Tone.FRIENDLY), Set.of(Interest.CULTURE, Interest.ART));
        Optional<Influencer> influencerOptional = Optional.of(new Influencer("1", twitter, null, null));

        when(mockInfluencerRepo.findById(anyString())).thenReturn(influencerOptional);
        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(null);

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Personality> result = influencerService.updatePersonality("1", personalityToUpdate);

        assertTrue(result.isPresent());
        assertEquals(personalityToUpdate, result.get());
    }

    @Test
    void updatePersonality_InvalidId_ReturnsConflictStatus() {
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Personality> result = influencerService.updatePersonality("1", new Personality(Set.of(Tone.FRIENDLY), Set.of(Interest.CULTURE, Interest.ART)));

        assertTrue(result.isEmpty());
    }

    @Test
    void updateAppearance_ValidRequest_ReturnsAcceptedStatus() {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        Appearance appearanceToUpdate = new Appearance(
                BodyBuild.ATHLETIC,
                EyeColor.GREEN,
                EyeShape.ALMOND,
                Set.of(FaceFeatures.BEARD, FaceFeatures.MOLE),
                FaceShape.OVAL,
                HairColor.BLACK,
                HairLength.MEDIUM,
                Height.AVERAGE,
                SkinTone.LIGHT,
                Style.BUSINESS
        );
        Optional<Influencer> influencerOptional = Optional.of(new Influencer("1", twitter, null, null));

        when(mockInfluencerRepo.findById(anyString())).thenReturn(influencerOptional);
        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(null);

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Appearance> result = influencerService.updateAppearance("1", appearanceToUpdate);

        assertTrue(result.isPresent());
        assertEquals(appearanceToUpdate, result.get());
    }

    @Test
    void updateAppearance_InvalidId_ReturnsConflictStatus() {
        Appearance appearanceToUpdate = new Appearance(
                BodyBuild.ATHLETIC,
                EyeColor.GREEN,
                EyeShape.ALMOND,
                Set.of(FaceFeatures.BEARD, FaceFeatures.MOLE),
                FaceShape.OVAL,
                HairColor.BLACK,
                HairLength.MEDIUM,
                Height.AVERAGE,
                SkinTone.LIGHT,
                Style.BUSINESS
        );

        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        Optional<Appearance> result = influencerService.updateAppearance("1", appearanceToUpdate);

        assertTrue(result.isEmpty());
    }
}