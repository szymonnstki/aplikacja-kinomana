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
            @RequestParam(name = "tab", required = false, defaultValue = "popular") String tab,
            Model model,
            Principal principal
    ) {
        List<Movie> movies;

        boolean hasSearch = search != null && !search.trim().isEmpty();

        boolean hasFilters = hasSearch
                || genre != null && !genre.isEmpty()
                || year != null
                || sort != null && !sort.isEmpty();

        if ("rated".equals(tab) && !hasFilters) {

            movies = movieService.findMoviesOrderByLocalRating();

        } else {
            List<TmdbMovieDto> tmdbResults;

            if (hasSearch) {
                tmdbResults = tmdbService.searchMoviesOnTmdb(search.trim());

                if (year != null) {
                    tmdbResults = tmdbResults.stream()
                            .filter(movie -> movie.getReleaseDate() != null
                                    && movie.getReleaseDate().startsWith(year.toString()))
                            .toList();
                }

                if (genre != null && !genre.isEmpty()) {
                    Integer genreId = Integer.parseInt(genre);

                    tmdbResults = tmdbResults.stream()
                            .filter(movie -> movie.getGenreIds() != null
                                    && movie.getGenreIds().contains(genreId))
                            .toList();
                }

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

            movies = tmdbResults.stream().map(dto -> {
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

                Movie localMovie = movieService.getMovieById(dto.getId());

                if (localMovie != null) {
                    m.setRatings(localMovie.getRatings());
                }

                return m;
            }).toList();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("activeTab", tab);
        model.addAttribute("isSearching", hasFilters);
        model.addAttribute("user", principal);

        return "index";
    }

    @GetMapping("/movies/details/{id}")
    public String movieDetails(@PathVariable Long id,
                               Model model,
                               Principal principal) {

        // Szukamy filmu w lokalnej bazie
        Movie movie = movieService.getMovieById(id);

        // Jeśli nie ma go w bazie, pobieramy z TMDB
        if (movie == null) {

            TmdbMovieDto dto = tmdbService.getMovieDetails(id);

            if (dto == null) {
                return "redirect:/";
            }

            movie = new Movie();

            movie.setId(dto.getId());
            movie.setTitle(dto.getTitle());
            movie.setDescription(dto.getOverview());
            movie.setDirector(tmdbService.getDirector(id));

            if (dto.getReleaseDate() != null
                    && dto.getReleaseDate().length() >= 4) {

                movie.setReleaseYear(
                        Integer.parseInt(
                                dto.getReleaseDate().substring(0, 4)
                        )
                );
            }

            if (dto.getPosterPath() != null) {
                movie.setImageUrl(
                        "https://image.tmdb.org/t/p/w500"
                                + dto.getPosterPath()
                );
            }

            movie.setGenre("TMDB");

            movieService.addMovie(movie);
        }

        // Film
        model.addAttribute("movie", movie);

        // Recenzje
        model.addAttribute(
                "ratings",
                ratingService.getRatingsForMovie(id)
        );

        // Aktualny użytkownik
        model.addAttribute("user", principal);

        // Status watchlisty
        String watchlistStatus = null;

        if (principal != null) {
            watchlistStatus =
                    watchlistService.getMovieStatusForUser(
                            principal.getName(),
                            id
                    );
        }

        model.addAttribute(
                "watchlistStatus",
                watchlistStatus
        );

        // PODOBNE FILMY Z TMDB
        model.addAttribute(
                "similarMovies",
                tmdbService.getSimilarMovies(id)
        );

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