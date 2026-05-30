package pl.kinoman.kinoman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pl.kinoman.kinoman.dto.TmdbMovieDto;
import pl.kinoman.kinoman.dto.TmdbResponse;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.service.TmdbService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TmdbServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TmdbService tmdbService;

    @BeforeEach
    void setUp() {
        tmdbService = new TmdbService();
        ReflectionTestUtils.setField(tmdbService, "apiKey", "test-key");
        ReflectionTestUtils.setField(tmdbService, "baseUrl", "https://api.themoviedb.org/3");
        ReflectionTestUtils.setField(tmdbService, "restTemplate", restTemplate);
    }

    @Test
    void searchMoviesOnTmdb_shouldReturnMovies() {
        TmdbMovieDto movie = new TmdbMovieDto();
        movie.setTitle("Matrix");
        TmdbResponse response = new TmdbResponse();
        response.setResults(List.of(movie));
        when(restTemplate.getForObject(anyString(), eq(TmdbResponse.class)))
                .thenReturn(response);
        List<TmdbMovieDto> result = tmdbService.searchMoviesOnTmdb("matrix");
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
    }

    @Test
    void getMovieDetails_whenApiThrowsException_shouldReturnNull() {
        when(restTemplate.getForObject(anyString(), eq(TmdbMovieDto.class)))
                .thenThrow(new RuntimeException());
        TmdbMovieDto result = tmdbService.getMovieDetails(10L);
        assertNull(result);
    }

    @Test
    void getDirector_whenDirectorExists_shouldReturnDirectorName() {
        Map<String, Object> director = new HashMap<>();
        director.put("job", "Director");
        director.put("name", "Christopher Nolan");
        Map<String, Object> response = new HashMap<>();
        response.put("crew", List.of(director));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(response);
        String result = tmdbService.getDirector(1L);
        assertEquals("Christopher Nolan", result);
    }

    @Test
    void getSimilarMovies_shouldReturnMovies() {
        TmdbMovieDto dto = new TmdbMovieDto();
        dto.setId(1L);
        dto.setTitle("Interstellar");
        dto.setReleaseDate("2014-11-07");
        TmdbResponse response = new TmdbResponse();
        response.setResults(List.of(dto));
        when(restTemplate.getForObject(contains("/similar"), eq(TmdbResponse.class)))
                .thenReturn(response);
        when(restTemplate.getForObject(contains("/credits"), eq(Map.class)))
                .thenReturn(Map.of("crew", List.of()));
        List<Movie> result = tmdbService.getSimilarMovies(1L);
        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
    }
}