package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.repository.MovieRepository;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public void addMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        // Zwraca film lub "null", jeśli nie znajdzie
        return movieRepository.findById(id).orElse(null);
    }
    // Dodaj to pod metodą getAllMovies()
    public java.util.List<Movie> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMovies(); // Jeśli pusto, zwróć wszystko
        }
        return movieRepository.findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(keyword, keyword);
    }
}