package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.model.Watchlist;
import pl.kinoman.kinoman.repository.WatchlistRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    // Dodaje lub aktualizuje status filmu na liście
    public void addToWatchlist(User user, Movie movie, String status) {
        Optional<Watchlist> existing = watchlistRepository.findByUserUsernameAndMovieId(user.getUsername(), movie.getId());
        if (existing.isPresent()) {
            Watchlist w = existing.get();
            w.setStatus(status);
            watchlistRepository.save(w);
        } else {
            Watchlist w = new Watchlist();
            w.setUser(user);
            w.setMovie(movie);
            w.setStatus(status);
            watchlistRepository.save(w);
        }
    }

    // Usuwa film z listy
    public void removeFromWatchlist(String username, Long movieId) {
        Optional<Watchlist> existing = watchlistRepository.findByUserUsernameAndMovieId(username, movieId);
        existing.ifPresent(watchlistRepository::delete);
    }

    // Pobiera listę konkretnego użytkownika
    public List<Watchlist> getUserWatchlist(String username) {
        return watchlistRepository.findByUserUsername(username);
    }

    // Sprawdza, czy użytkownik ma ten film na liście
    public String getMovieStatusForUser(String username, Long movieId) {
        Optional<Watchlist> existing = watchlistRepository.findByUserUsernameAndMovieId(username, movieId);
        return existing.map(Watchlist::getStatus).orElse(null);
    }
}