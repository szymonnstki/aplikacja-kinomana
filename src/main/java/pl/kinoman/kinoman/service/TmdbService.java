package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kinoman.kinoman.dto.TmdbResponse;
import pl.kinoman.kinoman.dto.TmdbMovieDto;

import java.util.Collections;
import java.util.List;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<TmdbMovieDto> searchMoviesOnTmdb(String query) {
        String url = String.format("%s/search/movie?api_key=%s&query=%s&language=pl-PL",
                baseUrl, apiKey, query);

        TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);
        return (response != null) ? response.getResults() : Collections.emptyList();
    }
    // Metoda do pobierania szczegółów jednego filmu po jego ID z TMDB
    public TmdbMovieDto getMovieDetails(Long id) {
        String url = String.format("%s/movie/%d?api_key=%s&language=pl-PL",
                baseUrl, id, apiKey);
        try {
            return restTemplate.getForObject(url, TmdbMovieDto.class);
        } catch (Exception e) {
            return null; // Jeśli filmu nie ma w TMDB, zwracamy null
        }
    }
}