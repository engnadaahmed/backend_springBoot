package com.example.movies.mapper;

import com.example.movies.model.Movie;
import jakarta.validation.Valid;

import java.util.List;

public class MovieListWrapper {
    @Valid // Ensures each Movie in the list is validated
    private List<Movie> movies;

    // Getters and Setters
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
