package com.example.movie.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.example.movie.model.FavoriteMovie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

    @Value("${api.token}")
    private String apiToken;

    @Value("${api.base-url}")
    private String apiBaseUrl;


    private final RedisService redisService;

   
    public ExternalApiService(RedisService redisService) {
        this.redisService = redisService;
    }


    public String getPopularMovies(String dayOrWeek) throws IOException, InterruptedException {
        String json="";
        if("week".equals(dayOrWeek)){
            // try to get data from Redis first
                json = (String) redisService.find("week");
                if(json==null){
                    logger.info("could not get trending WEEK data from Redis, it is either expired or not saved");
                    json = callApiWithAuth(apiBaseUrl + "/trending/movie/week", apiToken);
                    redisService.saveDataWithExpiration("week",json,10);
                    logger.info("got trending WEEK data from calling external API, and set expiration time for 10 seconds, for testing purpose");
                }else{
                    logger.info("retrieve data from Redis");
                    //logger.info(json);
                }
            }
        else if("day".equals(dayOrWeek)){
            json = (String) redisService.find("day");
            if(json==null){
                logger.info("could not get trending DAY data from Redis, it is either expired or not saved");
                json = callApiWithAuth(apiBaseUrl + "/trending/movie/day", apiToken);
                redisService.saveDataWithExpiration("day",json,10);
                logger.info("got trending DAY data from calling external API, and set expiration time for 10 seconds, for testing purpose");
            }else{
                logger.info("retrieve data from Redis");
                //logger.info(json);
            }
            
        }
        
        return json;
    }

  
    public String getMovieDetails(Long id) throws IOException, InterruptedException {
        String json = callApiWithAuth(apiBaseUrl + "/movie/" + id, apiToken);
        return json;
    }

    public String addFavoriteMovie(FavoriteMovie entity) throws IOException, InterruptedException {
        String json = callApiWithAuth(apiBaseUrl + "/account", apiToken);
        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
       
        String id = jsonObject.get("id").getAsString();
        String username = jsonObject.get("username").getAsString();
        logger.info(id);
        logger.info(username);
        return addFavoritePostCallApi(id, entity, apiToken);
    }

    public String addFavoritePostCallApi(String id, FavoriteMovie payload, String token) throws IOException,  InterruptedException {
        HttpClient httpClient= HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build();

        String url = apiBaseUrl+"/account/"+id+"/favorite";
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(payload);
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url)) // Replace with your API endpoint
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + token) // Add token
        .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // Set the method to POST and add the payload
        .build();
        //HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            // Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check HTTP status
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("Status Code: " , response.statusCode());
                logger.info("Response Body: " ,  response.body());
                return response.body();
            } else {
                throw new IOException("API returned status code: " + response.statusCode()
                        + " with body: " + response.body());
            }
        } catch (HttpTimeoutException e) {
            throw new IOException("Request timed out", e);
        }
       
       
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
