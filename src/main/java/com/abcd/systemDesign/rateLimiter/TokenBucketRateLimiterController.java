package com.abcd.systemDesign.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenBucketRateLimiterController {

    private final TokenBucketRateLimiterService rateLimiterService;

    @Autowired
    public TokenBucketRateLimiterController(TokenBucketRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/consume")
    public String consume(@RequestParam("userId") String userId, @RequestParam("tokens") long tokens) {
        boolean allowed = rateLimiterService.tryConsume(userId, tokens);
        return allowed ? "Request allowed" : "Rate limit exceeded";
    }
}
