package dev.elfa.backend.service;

import dev.elfa.backend.dto.AuthDto;
import dev.elfa.backend.dto.InfluencerResponseDto;
import dev.elfa.backend.dto.TwitterDto;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.repository.InfluencerRepo;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InfluencerServiceTest {
    private final InfluencerRepo mockInfluencerRepo = mock(InfluencerRepo.class);

    @Test
    void getInfluencerDto_ValidId_ReturnsInfluencerResponseDto() {
        // mocking data
        LocalDateTime currentDateTime = LocalDateTime.now();
        Auth auth = new Auth(true, "token", "secret", currentDateTime);
        Twitter twitter = new Twitter("1", "name", "username", auth);
        Influencer influencer = new Influencer("1", twitter, null, null);
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.of(influencer));

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // expected result
        AuthDto authDto = new AuthDto(true);
        TwitterDto twitterDto = new TwitterDto("1", "name", "username", authDto);
        InfluencerResponseDto influencerResponseDto = new InfluencerResponseDto("1", twitterDto);

        // run test
        Optional<InfluencerResponseDto> actualResponseDto = influencerService.getInfluencerDto("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualResponseDto, Optional.of(influencerResponseDto));
    }

    @Test
    void getInfluencerDto_IdNotFound_ReturnsEmpty() {
        // mocking data
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.empty());

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        Optional<InfluencerResponseDto> actualResponseDto = influencerService.getInfluencerDto("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertTrue(actualResponseDto.isEmpty());
    }

    @Test
    void getInfluencerDto_EmptyId_ReturnsEmpty() {
        // mocking data
        when(mockInfluencerRepo.findById("")).thenReturn(Optional.empty());

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        Optional<InfluencerResponseDto> actualResponseDto = influencerService.getInfluencerDto("");
        verify(mockInfluencerRepo, times(1)).findById("");
        assertEquals(actualResponseDto, Optional.empty());
    }

    @Test
    void getInfluencers_ValidPageable_ReturnsInfluencerResponseDtoPage() {
        // mocking data
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

        Auth auth1 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter1 = new Twitter("1", "name", "username", auth1);
        Influencer influencer1 = new Influencer("1", twitter1, null, null);

        Auth auth2 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter2 = new Twitter("1", "name", "username", auth2);
        Influencer influencer2 = new Influencer("1", twitter2, null, null);

        List<Influencer> influencerList = Arrays.asList(influencer1, influencer2);
        Page<Influencer> influencerPage = new PageImpl<>(influencerList);

        when(mockInfluencerRepo.findAll(pageable)).thenReturn(influencerPage);

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // expected result
        AuthDto authDto1 = new AuthDto(true);
        TwitterDto twitterDto1 = new TwitterDto("1", "name", "username", authDto1);
        InfluencerResponseDto influencerResponseDto1 = new InfluencerResponseDto("1", twitterDto1);

        AuthDto authDto2 = new AuthDto(true);
        TwitterDto twitterDto2 = new TwitterDto("1", "name", "username", authDto2);
        InfluencerResponseDto influencerResponseDto2 = new InfluencerResponseDto("1", twitterDto2);

        Page<InfluencerResponseDto> influencerResponseDtoPage = new PageImpl<>(Arrays.asList(influencerResponseDto1, influencerResponseDto2));

        // run test
        Page<InfluencerResponseDto> actualResponseDto = influencerService.getInfluencers(pageable);
        verify(mockInfluencerRepo, times(1)).findAll(pageable);
        assertEquals(actualResponseDto, influencerResponseDtoPage);
    }

    @Test
    void getInfluencers_EmptyList_ReturnsEmptyPage() {
        // mocking data
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

        when(mockInfluencerRepo.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        Page<InfluencerResponseDto> actualResponseDto = influencerService.getInfluencers(pageable);
        verify(mockInfluencerRepo, times(1)).findAll(pageable);
        assertEquals(actualResponseDto, new PageImpl<>(Collections.emptyList()));
    }

    @Test
    void getInfluencer_ValidId_ReturnsInfluencer() {
        // mocking data
        LocalDateTime currentDateTime = LocalDateTime.now();
        Auth auth = new Auth(true, "token", "secret", currentDateTime);
        Twitter twitter = new Twitter("1", "name", "username", auth);
        Influencer influencer = new Influencer("1", twitter, null, null);
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.of(influencer));

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        Optional<Influencer> actualInfluencer = influencerService.getInfluencer("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualInfluencer, Optional.of(influencer));
    }

    @Test
    void getInfluencer_IdNotFound_ReturnsEmpty() {
        // mocking data
        when(mockInfluencerRepo.findById("1")).thenReturn(Optional.empty());

        // service

        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        Optional<Influencer> actualInfluencer = influencerService.getInfluencer("1");
        verify(mockInfluencerRepo, times(1)).findById("1");
        assertEquals(actualInfluencer, Optional.empty());
    }

    @Test
    void deleteInfluencer_ValidId_DeletesInfluencer() {
        // mocking data
        doNothing().when(mockInfluencerRepo).deleteById("1");

        // service
        InfluencerService influencerService = new InfluencerService(mockInfluencerRepo);

        // run test
        boolean isDeleted = influencerService.deleteInfluencer("1");
        verify(mockInfluencerRepo, times(1)).deleteById("1");
        assertTrue(isDeleted);
    }
}