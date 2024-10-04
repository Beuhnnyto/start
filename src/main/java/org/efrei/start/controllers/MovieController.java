package org.efrei.start.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.efrei.start.dto.CreateMovie;
import org.efrei.start.dto.MovieResponse;
import org.efrei.start.models.Movie;
import org.efrei.start.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<MovieResponse> create(@RequestBody CreateMovie createMovie) {
        Movie movie = movieService.create(createMovie);
        return new ResponseEntity<>(convertToResponse(movie), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable String id) {
        Movie movie = movieService.getById(id);
        return movie != null ? new ResponseEntity<>(convertToResponse(movie), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAll() {
        List<Movie> movies = List.of(movieService.getAll());
        List<MovieResponse> movieResponses = movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(movieResponses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable String id, @RequestBody CreateMovie createMovie) {
        Movie updatedMovie = movieService.update(id, createMovie);
        return updatedMovie != null ? new ResponseEntity<>(convertToResponse(updatedMovie), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        movieService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Helper method to convert Movie to MovieResponse
    private MovieResponse convertToResponse(Movie movie) {
        Set<String> actorIds = movie.getActors().stream()
                .map(actor -> actor.getId()) // Get actor IDs
                .collect(Collectors.toSet());
        String directorId = movie.getDirector() != null ? movie.getDirector().getId() : null; // Get director ID
        return new MovieResponse(movie.getId(), movie.getTitle(), movie.getYear(), movie.getCategory(),
                movie.getRating(), actorIds, directorId);
    }
}
