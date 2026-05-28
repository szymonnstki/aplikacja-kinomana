package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kinoman.kinoman.dto.TmdbResponse;
import pl.kinoman.kinoman.dto.TmdbMovieDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public String getDirector(Long movieId) {

        String url = String.format("%s/movie/%d/credits?api_key=%s&language=pl-PL",
                baseUrl, movieId, apiKey);

        try {
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.get("crew") != null) {

                List<Map<String, Object>> crew =
                        (List<Map<String, Object>>) response.get("crew");

                for (Map<String, Object> person : crew) {
                    if ("Director".equals(person.get("job"))) {
                        return (String) person.get("name");
                    }
                }
            }

        } catch (Exception e) {
            return "Nieznany";
        }

        return "Nieznany";
    }

    public List<TmdbMovieDto> discoverMovies(String genreId, Integer year, String sortBy) {
        StringBuilder url = new StringBuilder(
                baseUrl + "/discover/movie?api_key=" + apiKey + "&language=pl-PL"
        );

        if (genreId != null && !genreId.isEmpty()) {
            url.append("&with_genres=").append(genreId);
        }

        if (year != null) {
            url.append("&primary_release_year=").append(year);
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            url.append("&sort_by=").append(sortBy);
        } else {
            url.append("&sort_by=popularity.desc");
        }

        TmdbResponse response = restTemplate.getForObject(url.toString(), TmdbResponse.class);
        return response != null ? response.getResults() : Collections.emptyList();
    }

}