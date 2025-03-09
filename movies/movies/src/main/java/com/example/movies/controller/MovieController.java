package com.example.movies.controller;

import com.example.movies.Repo.MovieRepository;
import com.example.movies.mapper.MovieListWrapper;
import com.example.movies.model.Movie;
import com.example.movies.services.OmdbService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin("http://localhost:4200")
@RestController
public class MovieController {

    @Autowired
    private    OmdbService omdbService;
    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/search")
    public Movie searchMovie(@RequestParam String title) {
        return omdbService.getMovieInfo(title);
    }
    @PostMapping("/movies")
    public ResponseEntity<Movie> addMovie( @Valid @RequestBody Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }

    // Batch add movies
   /* @PostMapping("/movies/batch")
    public ResponseEntity<List<Movie>> addMovies(@Valid @RequestBody MovieListWrapper movieListWrapper) {
        List<Movie> savedMovies = omdbService.addMovies(movieListWrapper.getMovies());
        return new ResponseEntity<>(savedMovies, HttpStatus.CREATED);
    }*/
    @PostMapping("/movies/batch")
    public ResponseEntity<List<Movie>> addMovies(@RequestParam("file") MultipartFile file) {
        try {
            // Debugging: Log the file name and size
            System.out.println("Received file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            // Read the file content
            String content = new String(file.getBytes());
            System.out.println("File content: " + content); // Debugging line
            // Parse the JSON content into a list of movies
            ObjectMapper objectMapper = new ObjectMapper();
            List<Movie> movies = objectMapper.readValue(content, new TypeReference<List<Movie>>() {});

            // Save the movies
            List<Movie> savedMovies = omdbService.addMovies(movies);
            return new ResponseEntity<>(savedMovies, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/movies")
    public ResponseEntity<Page<Movie>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Movie> movies = omdbService.getAllMovies(page, size);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id ){
        omdbService.deleteById(id);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/movies/batch")
    public ResponseEntity<String> deleteMovies(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids"); // Extract the list from the JSON object
        omdbService.deleteMoviesByIds(ids);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getById(@PathVariable("id") Long id ){
        Movie movie = omdbService.getById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);

    }
    @GetMapping("/movies/search")
    public ResponseEntity<Page<Movie>> searchMovies(
            @RequestParam String title, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
           ) {

        Page<Movie> result = omdbService.searchMoviesByTitle(title, page, size);
        return ResponseEntity.ok(result);
    }
}
