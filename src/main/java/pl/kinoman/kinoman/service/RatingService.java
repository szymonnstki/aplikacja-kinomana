package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kinoman.kinoman.model.Rating;
import pl.kinoman.kinoman.repository.RatingRepository;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    // ✅ dodaj lub zaktualizuj ocenę
    public void saveOrUpdateRating(Rating rating) {

        Optional<Rating> existing = ratingRepository
                .findByUserUsernameAndMovieId(
                        rating.getUser().getUsername(),
                        rating.getMovie().getId()
                );

        if (existing.isPresent()) {
            Rating r = existing.get();
            r.setStars(rating.getStars());
            r.setComment(rating.getComment());
            ratingRepository.save(r);
        } else {
            ratingRepository.save(rating);
        }
    }

    // 📄 wszystkie oceny filmu
    public List<Rating> getRatingsForMovie(Long movieId) {
        return ratingRepository.findByMovieId(movieId);
    }

    // 👤 oceny użytkownika
    public List<Rating> getRatingsByUser(String username) {
        return ratingRepository.findByUserUsername(username);
    }

    // ❌ usuń ocenę (tylko właściciel)
    public boolean deleteRating(Long ratingId, String username) {

        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);

        if (ratingOpt.isPresent()) {
            Rating rating = ratingOpt.get();

            if (rating.getUser().getUsername().equals(username)) {
                ratingRepository.delete(rating);
                return true;
            }
        }

        return false;
    }

    public boolean userHasRating(String username, Long movieId) {
        return ratingRepository.existsByUserUsernameAndMovieId(username, movieId);
    }
}