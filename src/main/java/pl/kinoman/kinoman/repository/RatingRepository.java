package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kinoman.kinoman.model.Rating;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Dodatkowa metoda: znajdź wszystkie oceny dla konkretnego filmu
    List<Rating> findByMovieId(Long movieId);
    List<Rating> findByUserUsername(String username);
}