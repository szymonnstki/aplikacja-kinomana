package pl.kinoman.kinoman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.repository.MovieRepository;
import pl.kinoman.kinoman.service.MovieService;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void addMovie_shouldSaveMovie() {
        Movie movie = new Movie();
        movie.setTitle("Matrix");
        movieService.addMovie(movie);
        verify(movieRepository).save(movie);
    }

    @Test
    void getMovieById_whenMovieExists_shouldReturnMovie() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));
        Movie result = movieService.getMovieById(1L);
        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void getMovieById_whenMovieDoesNotExist_shouldReturnNull() {
        when(movieRepository.findById(99L))
                .thenReturn(Optional.empty());
        Movie result = movieService.getMovieById(99L);
        assertNull(result);
    }
}