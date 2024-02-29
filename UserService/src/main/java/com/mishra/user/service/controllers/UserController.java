package com.mishra.user.service.controllers;


import com.mishra.user.service.entities.User;
import com.mishra.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user){
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    int retryCount = 1;

    @GetMapping("/{userId}")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
//    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getUserById(@PathVariable("userId") String userId){
        logger.info("Retry count: {}", retryCount);
        retryCount++;
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // creating fallback method for circuit breaker

    public ResponseEntity<User> ratingHotelFallback(String userId, Exception exception){
//        logger.info("Fallback is executed because service is down: ", exception);
        User user = User.builder()
                .email("dummy@gmail.com")
                .name("dummy")
                .about("This is user dummy beacause some service is down.")
                .userId("12232")
                .build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
