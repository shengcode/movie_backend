package com.example.movie.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.movie.model.FavoriteMovie;
import com.example.movie.service.ExternalApiService;


@RestController
@RequestMapping("/api")
public class ApiController {

  
    private final ExternalApiService externalApiService;

   
    public ApiController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/movies/list/display/{dayOrWeek}")
    public String getPopularMovies(@PathVariable String dayOrWeek) throws IOException, InterruptedException {
        return externalApiService.getPopularMovies(dayOrWeek);
    }

    @GetMapping("/movies/{id}")
    public String getMovieDetails(@PathVariable Long id) throws IOException, InterruptedException {
        return externalApiService.getMovieDetails(id);
    }
 
    @PostMapping("/movie/addFavorite")
    public ResponseEntity<String> addFavoriteMovie( @RequestBody FavoriteMovie entity) throws IOException, InterruptedException {
        String response= externalApiService.addFavoriteMovie(entity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
