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
    public String index(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "sort", required = false) String sort,
            Model model,
            Principal principal
    ) {
        List<TmdbMovieDto> tmdbResults;

        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasSearch) {
            tmdbResults = tmdbService.searchMoviesOnTmdb(search.trim());

            // FILTR ROKU
            if (year != null) {
                tmdbResults = tmdbResults.stream()
                        .filter(movie -> movie.getReleaseDate() != null
                                && movie.getReleaseDate().startsWith(year.toString()))
                        .toList();
            }

            // FILTR GATUNKU
            if (genre != null && !genre.isEmpty()) {
                Integer genreId = Integer.parseInt(genre);

                tmdbResults = tmdbResults.stream()
                        .filter(movie -> movie.getGenreIds() != null
                                && movie.getGenreIds().contains(genreId))
                        .toList();
            }

            // SORTOWANIE LOKALNE
            if ("release_date.desc".equals(sort)) {
                tmdbResults = tmdbResults.stream()
                        .sorted((a, b) -> String.valueOf(b.getReleaseDate())
                                .compareTo(String.valueOf(a.getReleaseDate())))
                        .toList();
            } else if ("release_date.asc".equals(sort)) {
                tmdbResults = tmdbResults.stream()
                        .sorted((a, b) -> String.valueOf(a.getReleaseDate())
                                .compareTo(String.valueOf(b.getReleaseDate())))
                        .toList();
            }

        } else {
            tmdbResults = tmdbService.discoverMovies(genre, year, sort);
        }

        List<Movie> movies = tmdbResults.stream().map(dto -> {
            Movie m = new Movie();

            m.setId(dto.getId());
            m.setTitle(dto.getTitle());
            m.setDescription(dto.getOverview());
            m.setGenre("Film z TMDB");
            m.setDirector(tmdbService.getDirector(dto.getId()));

            if (dto.getReleaseDate() != null && dto.getReleaseDate().length() >= 4) {
                m.setReleaseYear(Integer.parseInt(dto.getReleaseDate().substring(0, 4)));
            }

            if (dto.getPosterPath() != null) {
                m.setImageUrl("https://image.tmdb.org/t/p/w500" + dto.getPosterPath());
            }

            return m;
        }).toList();

        model.addAttribute("movies", movies);
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedSort", sort);
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
                movie.setDirector(tmdbService.getDirector(id));

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

    @PostMapping("/movies/details/rate/add/{id}")
    public String addOrUpdateRating(@PathVariable Long id,
                                    @RequestParam int stars,
                                    @RequestParam String comment,
                                    Principal principal) {

        Movie movie = movieService.getMovieById(id);
        User user = userService.findByUsername(principal.getName());

        if (movie != null && user != null) {

            Rating rating = new Rating();
            rating.setMovie(movie);
            rating.setUser(user);
            rating.setStars(stars);
            rating.setComment(comment);

            ratingService.saveOrUpdateRating(rating);
        }

        return "redirect:/movies/details/" + id;
    }

    @PostMapping("/movies/details/rate/delete/{id}")
    public String deleteRating(@PathVariable Long id,
                               @RequestParam Long movieId,
                               Principal principal) {

        if (principal != null) {
            ratingService.deleteRating(id, principal.getName());
        }

        return "redirect:/movies/details/" + movieId;
    }
}