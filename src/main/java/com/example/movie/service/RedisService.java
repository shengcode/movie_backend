package com.example.movie.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void saveDataWithExpiration(String key, Object value, int timeout) {
        redisTemplate.opsForValue().set(key, value,timeout,TimeUnit.SECONDS);
    }

    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
