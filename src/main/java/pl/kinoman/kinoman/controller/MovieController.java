package pl.kinoman.kinoman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.kinoman.kinoman.model.Movie;
import pl.kinoman.kinoman.model.Rating;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.service.*;
import pl.kinoman.kinoman.dto.TmdbMovieDto; // DODANY IMPORT

import java.security.Principal;
import java.util.List; // DODANY IMPORT

@Controller
public class MovieController {

    @Autowired private MovieService movieService;
    @Autowired private UserService userService;
    @Autowired private RatingService ratingService;
    @Autowired private WatchlistService watchlistService;
    @Autowired private TmdbService tmdbService;

    @GetMapping("/")
    public String index(@RequestParam(name = "search", required = false) String search, Model model, Principal principal) {
        if (search != null && !search.isEmpty()) {
            // SZUKAMY W TMDB ZAMIAST W LOKALNEJ BAZIE
            List<TmdbMovieDto> tmdbResults = tmdbService.searchMoviesOnTmdb(search);

            // Konwertujemy wyniki z TMDB na nasze obiekty Movie
            List<Movie> movies = tmdbResults.stream().map(dto -> {
                Movie m = new Movie();
                // UWAGA: przypisujemy ID z TMDB do naszego filmu na czas wyświetlania!
                m.setId(dto.getId());
                m.setTitle(dto.getTitle());
                m.setDescription(dto.getOverview());
                m.setGenre("Film z TMDB");
                if(dto.getReleaseDate() != null && dto.getReleaseDate().length() >= 4) {
                    m.setReleaseYear(Integer.parseInt(dto.getReleaseDate().substring(0, 4)));
                }
                if (dto.getPosterPath() != null) {
                    m.setImageUrl("https://image.tmdb.org/t/p/w500" + dto.getPosterPath());
                }
                return m;
            }).toList();

            model.addAttribute("movies", movies);
            model.addAttribute("searchQuery", search);
        } else {
            model.addAttribute("movies", movieService.getAllMovies());
        }

        model.addAttribute("user", principal);
        return "index";
    }

    @GetMapping("/movies/details/{id}")
    public String movieDetails(@PathVariable Long id, Model model, Principal principal) {
        // 1. Szukamy filmu w naszej lokalnej bazie MySQL
        Movie movie = movieService.getMovieById(id);

        // 2. Jeśli filmu NIE MA w bazie, pobieramy go z TMDB i zapisujemy u siebie
        if (movie == null) {
            TmdbMovieDto dto = tmdbService.getMovieDetails(id);
            if (dto != null) {
                movie = new Movie();
                movie.setId(dto.getId()); // Ustawiamy ID z TMDB
                movie.setTitle(dto.getTitle());
                movie.setDescription(dto.getOverview());
                movie.setGenre("Film z TMDB");
                movie.setDirector("Nieznany (TMDB API)");
                if(dto.getReleaseDate() != null && dto.getReleaseDate().length() >= 4) {
                    movie.setReleaseYear(Integer.parseInt(dto.getReleaseDate().substring(0, 4)));
                }
                if (dto.getPosterPath() != null) {
                    movie.setImageUrl("https://image.tmdb.org/t/p/w500" + dto.getPosterPath());
                }

                // ZAPISUJEMY W BAZIE - teraz film ma swoje ID w MySQL i można go oceniać!
                movieService.addMovie(movie);
            } else {
                return "redirect:/"; // Jeśli filmu nie ma nawet w TMDB, wracamy na główną
            }
        }

        // 3. Reszta pozostaje bez zmian - wyświetlamy stronę
        model.addAttribute("movie", movie);
        model.addAttribute("ratings", ratingService.getRatingsForMovie(id));
        model.addAttribute("user", principal);

        String watchlistStatus = null;
        if (principal != null) {
            watchlistStatus = watchlistService.getMovieStatusForUser(principal.getName(), id);
        }
        model.addAttribute("watchlistStatus", watchlistStatus);

        return "movie-details";
    }

    @PostMapping("/movies/details/{id}/rate")
    public String addRating(@PathVariable Long id, @RequestParam int stars, @RequestParam String comment, Principal principal) {
        Movie movie = movieService.getMovieById(id);
        User user = userService.findByUsername(principal.getName());
        if (movie != null && user != null) {
            Rating rating = new Rating();
            rating.setMovie(movie);
            rating.setUser(user);
            rating.setStars(stars);
            rating.setComment(comment);
            ratingService.saveRating(rating);
        }
        return "redirect:/movies/details/" + id;
    }
}