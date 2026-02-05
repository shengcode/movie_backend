package com.example.movie.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiService {

    @Value("${api.token}")
    private String apiToken;

    @Value("${api.base-url}")
    private String apiBaseUrl;


    public String getPopularMovies() throws IOException, InterruptedException {
        System.out.println("lalalal");
        String json = callApiWithAuth(apiBaseUrl + "", apiToken);
        
        return json;
    }

  
    public String getMovieDetails(Long id) throws IOException, InterruptedException {
        String json = callApiWithAuth(apiBaseUrl + "/movie/" + id, apiToken);
        return json;
    }

    public static String callApiWithAuth(String url, String token)
            throws IOException, InterruptedException {

        // Create HttpClient with timeout
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        // Build request with Authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + token) // Add token
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check HTTP status
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                throw new IOException("API returned status code: " + response.statusCode()
                        + " with body: " + response.body());
            }
        } catch (HttpTimeoutException e) {
            throw new IOException("Request timed out", e);
        }
    }

}
