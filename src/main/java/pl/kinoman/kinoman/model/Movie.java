package pl.kinoman.kinoman.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "movies")

public class Movie {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String director;
    private int releaseYear;

    @Column(length = 1000)
    private String description;

    private String genre;


    // Powiązanie z ocenami (Jeden film ma wiele ocen)
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private java.util.List<Rating> ratings;

    // Metoda wyliczająca średnią (Thymeleaf odczyta ją jako "averageRating")
    public String getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return "Brak"; // Jeśli nikt nie ocenił filmu
        }

        double sum = 0;
        for (Rating r : ratings) {
            sum += r.getStars();
        }
        double avg = sum / ratings.size();

        // Zaokrągla do jednego miejsca po przecinku (np. 8.5)
        return String.format("%.1f", avg);
    }

    // Potrzebujemy też gettera i settera dla samej listy (żeby uniknąć "cannot find symbol")
    public java.util.List<Rating> getRatings() { return ratings; }
    public void setRatings(java.util.List<Rating> ratings) { this.ratings = ratings; }


    // Dodaj to w klasie Movie.java
    private String imageUrl;

    // I pamiętaj o getterze/setterze:
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}