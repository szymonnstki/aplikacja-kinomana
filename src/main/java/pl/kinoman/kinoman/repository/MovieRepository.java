package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kinoman.kinoman.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Dodaj tę linijkę (szuka części tytułu LUB części gatunku):
    List<Movie> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(String title, String genre);
}