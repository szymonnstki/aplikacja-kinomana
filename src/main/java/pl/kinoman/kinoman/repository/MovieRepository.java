package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kinoman.kinoman.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Dodaj tę linijkę (szuka części tytułu LUB części gatunku):
    List<Movie> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(String title, String genre);
    @Query("""
        SELECT m
        FROM Movie m
        JOIN m.ratings r
        GROUP BY m
        ORDER BY AVG(r.stars) DESC
    """)
    List<Movie> findMoviesOrderByLocalRating();

}