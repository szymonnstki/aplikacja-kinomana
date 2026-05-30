package pl.kinoman.kinoman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.model.Watchlist;
import pl.kinoman.kinoman.repository.WatchlistRepository;
import pl.kinoman.kinoman.service.WatchlistService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    @Test
    void addToWatchlist_whenMovieAlreadyExists_shouldUpdateStatus() {
        User user = new User();
        user.setUsername("jan");
        Movie movie = new Movie();
        movie.setId(1L);
        Watchlist existing = new Watchlist();
        existing.setUser(user);
        existing.setMovie(movie);
        existing.setStatus("PLAN_TO_WATCH");
        when(watchlistRepository.findByUserUsernameAndMovieId("jan", 1L))
                .thenReturn(Optional.of(existing));
        watchlistService.addToWatchlist(user, movie, "WATCHED");
        assertEquals("WATCHED", existing.getStatus());
        verify(watchlistRepository).save(existing);
    }

    @Test
    void addToWatchlist_whenMovieDoesNotExist_shouldCreateNewWatchlist() {
        User user = new User();
        user.setUsername("jan");
        Movie movie = new Movie();
        movie.setId(1L);
        when(watchlistRepository.findByUserUsernameAndMovieId("jan", 1L))
                .thenReturn(Optional.empty());
        watchlistService.addToWatchlist(user, movie, "WATCHING");
        verify(watchlistRepository).save(any(Watchlist.class));
    }

    @Test
    void getMovieStatusForUser_whenMovieExists_shouldReturnStatus() {
        Watchlist watchlist = new Watchlist();
        watchlist.setStatus("WATCHED");
        when(watchlistRepository.findByUserUsernameAndMovieId("jan", 1L))
                .thenReturn(Optional.of(watchlist));
        String result = watchlistService.getMovieStatusForUser("jan", 1L);
        assertEquals("WATCHED", result);
    }
}