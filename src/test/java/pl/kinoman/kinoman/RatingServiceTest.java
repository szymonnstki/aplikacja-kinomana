package pl.kinoman.kinoman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.model.Rating;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.repository.RatingRepository;
import pl.kinoman.kinoman.service.RatingService;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void saveOrUpdateRating_whenRatingExists_shouldUpdateRating() {
        User user = new User();
        user.setUsername("jan");
        Movie movie = new Movie();
        movie.setId(1L);
        Rating oldRating = new Rating();
        oldRating.setUser(user);
        oldRating.setMovie(movie);
        oldRating.setStars(3);
        oldRating.setComment("stary komentarz");
        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setMovie(movie);
        newRating.setStars(5);
        newRating.setComment("super film");
        when(ratingRepository.findByUserUsernameAndMovieId("jan", 1L))
                .thenReturn(Optional.of(oldRating));
        ratingService.saveOrUpdateRating(newRating);
        assertEquals(5, oldRating.getStars());
        assertEquals("super film", oldRating.getComment());
        verify(ratingRepository).save(oldRating);
    }

    @Test
    void saveOrUpdateRating_whenRatingDoesNotExist_shouldSaveNewRating() {
        User user = new User();
        user.setUsername("jan");
        Movie movie = new Movie();
        movie.setId(1L);
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setStars(4);
        rating.setComment("ok");
        when(ratingRepository.findByUserUsernameAndMovieId("jan", 1L))
                .thenReturn(Optional.empty());
        ratingService.saveOrUpdateRating(rating);
        verify(ratingRepository).save(rating);
    }

    @Test
    void deleteRating_whenUserIsOwner_shouldDeleteAndReturnTrue() {
        User user = new User();
        user.setUsername("jan");
        Rating rating = new Rating();
        rating.setUser(user);
        when(ratingRepository.findById(1L))
                .thenReturn(Optional.of(rating));
        boolean result = ratingService.deleteRating(1L, "jan");
        assertTrue(result);
        verify(ratingRepository).delete(rating);
    }

    @Test
    void deleteRating_whenUserIsNotOwner_shouldReturnFalse() {
        User user = new User();
        user.setUsername("jan");
        Rating rating = new Rating();
        rating.setUser(user);
        when(ratingRepository.findById(1L))
                .thenReturn(Optional.of(rating));

        boolean result = ratingService.deleteRating(1L, "anna");
        assertFalse(result);
        verify(ratingRepository, never()).delete(any());
    }
}