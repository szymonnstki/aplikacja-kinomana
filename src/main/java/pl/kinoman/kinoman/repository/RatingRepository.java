package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kinoman.kinoman.model.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByMovieId(Long movieId);

    List<Rating> findByUserUsername(String username);

    // 🔥 KLUCZOWE – sprawdzanie czy user już ocenił film
    Optional<Rating> findByUserUsernameAndMovieId(String username, Long movieId);

    // (opcjonalnie, ale czytelniejsze niż findById)
    Optional<Rating> findById(Long id);

    boolean existsByUserUsernameAndMovieId(String username, Long movieId);


}