package pl.kinoman.kinoman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TmdbMovieDto {
    private Long id;
    private String title;
    private String overview;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    // --- GETTERY I SETTERY ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
}