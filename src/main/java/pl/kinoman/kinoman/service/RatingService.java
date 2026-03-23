package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kinoman.kinoman.model.Rating;
import pl.kinoman.kinoman.repository.RatingRepository;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public void saveRating(Rating rating) {
        ratingRepository.save(rating);
    }

    public List<Rating> getRatingsForMovie(Long movieId) {
        return ratingRepository.findByMovieId(movieId);
    }
    public List<Rating> getRatingsByUser(String username) {
        return ratingRepository.findByUserUsername(username);
    }
}