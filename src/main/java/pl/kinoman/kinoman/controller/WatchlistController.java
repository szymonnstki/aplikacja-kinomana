package pl.kinoman.kinoman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.service.MovieService;
import pl.kinoman.kinoman.service.UserService;
import pl.kinoman.kinoman.service.WatchlistService;

import java.security.Principal;

@Controller
public class WatchlistController {

    @Autowired private WatchlistService watchlistService;
    @Autowired private UserService userService;
    @Autowired private MovieService movieService;

    @PostMapping("/watchlist/add")
    public String addOrUpdate(@RequestParam Long movieId, @RequestParam String status, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            Movie movie = movieService.getMovieById(movieId);
            if (user != null && movie != null) {
                watchlistService.addToWatchlist(user, movie, status);
            }
        }
        return "redirect:/movies/details/" + movieId;
    }

    @PostMapping("/watchlist/remove")
    public String remove(@RequestParam Long movieId, Principal principal) {
        if (principal != null) {
            watchlistService.removeFromWatchlist(principal.getName(), movieId);
        }
        return "redirect:/movies/details/" + movieId;
    }
}