package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kinoman.kinoman.model.Watchlist;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserUsername(String username);
    Optional<Watchlist> findByUserUsernameAndMovieId(String username, Long movieId);
}