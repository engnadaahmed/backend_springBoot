package com.example.movies.services;

import com.example.movies.Repo.MovieRepository;
import com.example.movies.dto.MovieResponse;
import com.example.movies.exception.NOT_FOUND_ID;
import com.example.movies.model.Movie;
import com.example.movies.model.Rating;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OmdbService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MovieRepository movieRepository;

    @Value("${omdb.api.key}")
    private String apiKey;

    private static final String OMDB_URL = "http://www.omdbapi.com/?apikey={apiKey}&t={title}";

    public Movie getMovieInfo(String title) {
        // Fetch movie data from OMDB API
        MovieResponse response = restTemplate.getForObject(OMDB_URL, MovieResponse.class, apiKey, title);

        if (response != null && "True".equalsIgnoreCase(response.getResponse())) {
            // Map the response to the Movie entity
            Movie movie = new Movie();
            movie.setTitle(response.getTitle());
            movie.setYear(response.getYear());
            movie.setRated(response.getRated());
            movie.setReleased(response.getReleased());
            movie.setRuntime(response.getRuntime());
            movie.setGenre(response.getGenre());
            movie.setDirector(response.getDirector());
            movie.setWriter(response.getWriter());
            movie.setActors(response.getActors());
            movie.setPlot(response.getPlot());
            movie.setLanguage(response.getLanguage());
            movie.setCountry(response.getCountry());
            movie.setAwards(response.getAwards());
            movie.setPoster(response.getPoster());
            movie.setMetascore(response.getMetascore());
            movie.setImdbRating(response.getImdbRating());
            movie.setImdbVotes(response.getImdbVotes());
            movie.setImdbID(response.getImdbID());
            movie.setType(response.getType());
            movie.setDvd(response.getDvd());
            movie.setBoxOffice(response.getBoxOffice());
            movie.setProduction(response.getProduction());
            movie.setWebsite(response.getWebsite());
            movie.setResponse(response.getResponse());

            // Map Ratings
            if (response.getRatings() != null) {
                List<Rating> ratings = response.getRatings().stream()
                        .map(r -> {
                            Rating rating = new Rating();
                            rating.setSource(r.getSource());
                            rating.setValue(r.getValue());
                            return rating;
                        })
                        .toList();
                movie.setRatings(ratings);
            }

            // Save the movie to the database
            return movieRepository.save(movie);
        } else {
            throw new RuntimeException("Movie not found or API error: " + (response != null ? response.getResponse() : "No response"));
        }
    }
    public Movie getById(Long id){

        return movieRepository.findById(id).orElseThrow(()-> new NOT_FOUND_ID("not found id of movie:" + id));
    }

    public void  deleteById(Long id) {

        Movie movie = getById(id);
        if (movie != null) {
            movieRepository.deleteById(id);
        }

    }

    // Batch add movies
    @Transactional
    public List<Movie> addMovies(List<Movie> movies) {
        return movieRepository.saveAll(movies);
    }

    // Batch remove movies by IDs
    @Transactional
    public void deleteMoviesByIds(List<Long> ids) {
        // Check if all IDs exist in the database
        List<Movie> moviesToDelete = movieRepository.findAllById(ids);
        if (moviesToDelete.size() != ids.size()) {
            throw new NOT_FOUND_ID("One or more movie IDs not found");
        }

        // Delete the movies
        movieRepository.deleteAllById(ids);
    }
//get all movies in pagination
    public Page<Movie> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    public Page<Movie> searchMoviesByTitle(String title, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findByTitleContainingIgnoreCase(title, pageable);
    }
}

