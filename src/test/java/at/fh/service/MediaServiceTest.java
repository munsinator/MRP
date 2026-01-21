package at.fh.service;

import at.fh.dto.MediaDetailsResponse;
import at.fh.dto.MediaInput;
import at.fh.dto.MediaSearchParams;
import at.fh.model.Genre;
import at.fh.model.MediaEntry;
import at.fh.constants.MediaType;
import at.fh.model.Rating;
import at.fh.repository.GenreRepository;
import at.fh.repository.MediaEntryRepository;
import at.fh.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MediaServiceTest {

    @Mock
    MediaEntryRepository mediaRepository;
    @Mock
    GenreRepository genreRepository;
    @Mock
    RatingRepository ratingRepository;

    @InjectMocks
    MediaService service;

    private MediaEntry media(UUID id, UUID createdBy) {
        return new MediaEntry.Builder()
                .id(id)
                .createdBy(createdBy)
                .title("T")
                .description("D")
                .releaseYear(2010)
                .ageRestriction(12)
                .mediaType(MediaType.MOVIE)
                .build();
    }

    // 1) findAll delegates
    @Test
    void findAll_delegatesToRepo() {
        List<MediaEntry> list = List.of(media(UUID.randomUUID(), UUID.randomUUID()));
        when(mediaRepository.findAll()).thenReturn(list);

        List<MediaEntry> out = service.findAll();

        assertSame(list, out);
        verify(mediaRepository).findAll();
        verifyNoMoreInteractions(mediaRepository, genreRepository, ratingRepository);
    }

    // 2) findById empty -> empty
    @Test
    void findById_notFound_returnsEmpty_andDoesNotLoadGenres() {
        UUID id = UUID.randomUUID();
        when(mediaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<MediaEntry> out = service.findById(id);

        assertTrue(out.isEmpty());
        verify(mediaRepository).findById(id);
        verifyNoInteractions(genreRepository);
    }

    // 3) findById found -> attaches genres
    @Test
    void findById_found_attachesGenres() {
        UUID id = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        MediaEntry base = media(id, user);

        List<Genre> genres = List.of(
                new Genre.Builder().id(UUID.randomUUID()).name("Sci-Fi").build(),
                new Genre.Builder().id(UUID.randomUUID()).name("Action").build()
        );

        when(mediaRepository.findById(id)).thenReturn(Optional.of(base));
        when(genreRepository.findByMediaId(id)).thenReturn(genres);

        MediaEntry out = service.findById(id).orElseThrow();

        assertEquals(id, out.getId());
        assertEquals(user, out.getCreatedBy());
        assertEquals("T", out.getTitle());
        assertEquals(genres, out.getGenres());
        verify(mediaRepository).findById(id);
        verify(genreRepository).findByMediaId(id);
    }

    // 4) create: uses existing genre if present
    @Test
    void create_usesExistingGenre_doesNotSaveGenreAgain() {
        UUID userId = UUID.randomUUID();
        Genre existing = new Genre.Builder().id(UUID.randomUUID()).name("Sci-Fi").build();

        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of("Sci-Fi"), MediaType.MOVIE);

        when(genreRepository.findByName("Sci-Fi")).thenReturn(Optional.of(existing));
        when(mediaRepository.save(any())).thenReturn(true);

        boolean ok = service.create(in, userId);

        assertTrue(ok);
        verify(genreRepository).findByName("Sci-Fi");
        verify(genreRepository, never()).save(any());
        verify(mediaRepository).save(any(MediaEntry.class));
    }

    // 5) create: saves new genre if missing
    @Test
    void create_createsAndSavesGenreIfMissing() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of("NewGenre"), MediaType.MOVIE);

        when(genreRepository.findByName("NewGenre")).thenReturn(Optional.empty());
        when(mediaRepository.save(any())).thenReturn(true);

        boolean ok = service.create(in, userId);

        assertTrue(ok);
        verify(genreRepository).save(any(Genre.class));
        verify(mediaRepository).save(any(MediaEntry.class));
    }

    // 6) create: multiple genres -> lookup each
    @Test
    void create_multipleGenres_looksUpEach() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of("A", "B", "C"), MediaType.MOVIE);

        when(genreRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(mediaRepository.save(any())).thenReturn(true);

        service.create(in, userId);

        verify(genreRepository).findByName("A");
        verify(genreRepository).findByName("B");
        verify(genreRepository).findByName("C");
    }

    // 7) create: null releaseYear -> 0
    @Test
    void create_nullReleaseYear_defaultsToZero() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", null, 12, List.of(), MediaType.MOVIE);

        when(mediaRepository.save(any())).thenReturn(true);

        service.create(in, userId);

        ArgumentCaptor<MediaEntry> cap = ArgumentCaptor.forClass(MediaEntry.class);
        verify(mediaRepository).save(cap.capture());
        assertEquals(0, cap.getValue().getReleaseYear());
    }

    // 8) create: null ageRestriction -> 0
    @Test
    void create_nullAgeRestriction_defaultsToZero() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, null, List.of(), MediaType.MOVIE);

        when(mediaRepository.save(any())).thenReturn(true);

        service.create(in, userId);

        ArgumentCaptor<MediaEntry> cap = ArgumentCaptor.forClass(MediaEntry.class);
        verify(mediaRepository).save(cap.capture());
        assertEquals(0, cap.getValue().getAgeRestriction());
    }

    // 9) create: sets createdBy = userId
    @Test
    void create_setsCreatedByToUserId() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of(), MediaType.MOVIE);

        when(mediaRepository.save(any())).thenReturn(true);

        service.create(in, userId);

        ArgumentCaptor<MediaEntry> cap = ArgumentCaptor.forClass(MediaEntry.class);
        verify(mediaRepository).save(cap.capture());
        assertEquals(userId, cap.getValue().getCreatedBy());
    }

    // 10) create: returns repo result
    @Test
    void create_returnsFalseIfRepoSaveFails() {
        UUID userId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of(), MediaType.MOVIE);

        when(mediaRepository.save(any())).thenReturn(false);

        assertFalse(service.create(in, userId));
    }

    // 11) update: not found -> false and no update call
    @Test
    void update_notFound_returnsFalse() {
        UUID mediaId = UUID.randomUUID();
        MediaInput in = new MediaInput("t", "d", 2010, 12, List.of(), MediaType.MOVIE);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        assertFalse(service.update(in, mediaId));
        verify(mediaRepository).findById(mediaId);
        verify(mediaRepository, never()).update(any());
    }

    // 12) update: keeps createdBy + createdAt from existing
    @Test
    void update_keepsCreatedByAndCreatedAt() {
        UUID mediaId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        MediaEntry existing = new MediaEntry.Builder()
                .id(mediaId)
                .createdBy(createdBy)
                .createdAt(java.time.LocalDateTime.now())
                .title("old")
                .description("old")
                .releaseYear(2000)
                .ageRestriction(6)
                .mediaType(MediaType.MOVIE)
                .build();

        MediaInput in = new MediaInput("new", "new", 2010, 12, List.of(), MediaType.SERIES);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        when(mediaRepository.update(any())).thenReturn(true);

        assertTrue(service.update(in, mediaId));

        ArgumentCaptor<MediaEntry> cap = ArgumentCaptor.forClass(MediaEntry.class);
        verify(mediaRepository).update(cap.capture());
        MediaEntry updated = cap.getValue();

        assertEquals(createdBy, updated.getCreatedBy());
        assertEquals(existing.getCreatedAt(), updated.getCreatedAt());
        assertEquals("new", updated.getTitle());
        assertEquals("new", updated.getDescription());
    }

    // 13) update: null releaseYear -> 0
    @Test
    void search_delegatesToRepo() {
        MediaSearchParams p = new MediaSearchParams("t", "g", "MOVIE", 2010, 12, 4.0, "score");
        when(mediaRepository.findByParams(p)).thenReturn(List.of());

        service.search(p);

        verify(mediaRepository).findByParams(p);
    }

    // 14) update: null ageRestriction -> 0
    @Test
    void update_nullAgeRestriction_defaultsToZero() {
        UUID mediaId = UUID.randomUUID();
        MediaEntry existing = media(mediaId, UUID.randomUUID());
        MediaInput in = new MediaInput("t", "d", 2010, null, List.of(), MediaType.MOVIE);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        when(mediaRepository.update(any())).thenReturn(true);

        service.update(in, mediaId);

        ArgumentCaptor<MediaEntry> cap = ArgumentCaptor.forClass(MediaEntry.class);
        verify(mediaRepository).update(cap.capture());
        assertEquals(0, cap.getValue().getAgeRestriction());
    }

    // 15) delete delegates
    @Test
    void delete_delegates() {
        UUID id = UUID.randomUUID();
        when(mediaRepository.delete(id)).thenReturn(true);

        assertTrue(service.delete(id));
        verify(mediaRepository).delete(id);
    }

    // 16) getFavoriteMediaFrom delegates
    @Test
    void favorites_delegates() {
        UUID uid = UUID.randomUUID();
        when(mediaRepository.getFavoriteMediaFrom(uid)).thenReturn(List.of());

        service.getFavoriteMediaFrom(uid);
        verify(mediaRepository).getFavoriteMediaFrom(uid);
    }

    // 17) likeMedia
    @Test
    void likeMedia_delegatesToRepo() {
        UUID mid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(mediaRepository.favoriteMedia(uid, mid)).thenReturn(true);

        assertTrue(service.likeMedia(mid, uid));
        verify(mediaRepository).favoriteMedia(uid, mid);
    }

    // 18) unlikeMedia
    @Test
    void unlikeMedia_delegatesToRepo() {
        UUID mid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(mediaRepository.unfavoriteMedia(uid, mid)).thenReturn(true);

        assertTrue(service.unlikeMedia(mid, uid));
        verify(mediaRepository).unfavoriteMedia(uid, mid);
    }

    // 19) getDetails not found -> empty and no rating calls
    @Test
    void getDetails_notFound_returnsEmpty_andNoRatingCalls() {
        UUID mid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(mediaRepository.findById(mid)).thenReturn(Optional.empty());

        Optional<MediaDetailsResponse> out = service.getDetails(mid, uid);

        assertTrue(out.isEmpty());
        verify(mediaRepository).findById(mid);
        verifyNoInteractions(ratingRepository);
    }

    // 20) getDetails found -> fills avg + ratings + mediaType name
    @Test
    void getDetails_found_populatesResponse() {
        UUID mid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        MediaEntry m = media(mid, UUID.randomUUID());

        List<Rating> ratings = List.of(
                new Rating.Builder()
                        .id(UUID.randomUUID())
                        .createdBy(uid)
                        .mediaId(mid)
                        .isPublic(true)
                        .stars(5)
                        .comment("ok")
                        .createdAt(null)
                        .updatedAt(null)
                        .build(),
                new Rating.Builder()
                        .id(UUID.randomUUID())
                        .createdBy(uid)
                        .mediaId(mid)
                        .isPublic(false)
                        .stars(3)
                        .comment(null)
                        .createdAt(null)
                        .updatedAt(null)
                        .build()
        );

        when(mediaRepository.findById(mid)).thenReturn(Optional.of(m));
        when(ratingRepository.getAveragePublicScoreForMedia(mid)).thenReturn(4.2);
        when(ratingRepository.findVisibleRatingsForMedia(mid, uid)).thenReturn(ratings);

        MediaDetailsResponse resp = service.getDetails(mid, uid).orElseThrow();

        assertEquals(mid, resp.id());
        assertEquals("T", resp.title());
        assertEquals(4.2, resp.averageScore());
        assertEquals(ratings, resp.ratings());
        assertEquals("MOVIE", resp.mediaType());
        verify(ratingRepository).getAveragePublicScoreForMedia(mid);
        verify(ratingRepository).findVisibleRatingsForMedia(mid, uid);
    }
}
