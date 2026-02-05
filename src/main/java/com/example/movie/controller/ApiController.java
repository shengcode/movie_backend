package com.example.movie.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie.service.ExternalApiService;

@RestController
@RequestMapping("/api")
public class ApiController {

  
    private final ExternalApiService externalApiService;

   
    public ApiController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/movies/list/display")
    public String getPopularMovies() throws IOException, InterruptedException {
        return externalApiService.getPopularMovies();
    }

    @GetMapping("/movies/{id}")
    public String getMovieDetails(@PathVariable Long id) throws IOException, InterruptedException {
        return externalApiService.getMovieDetails(id);
    }
}
