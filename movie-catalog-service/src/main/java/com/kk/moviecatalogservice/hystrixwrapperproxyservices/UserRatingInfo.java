package com.kk.moviecatalogservice.hystrixwrapperproxyservices;

import com.google.common.collect.ImmutableList;
import com.kk.moviecatalogservice.models.Rating;
import com.kk.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackUserRating",
    commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"), // timeout
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // look for last n requests
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"), // break if 50 % of the requests fail
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") // sleeps for 5 seconds then takes other requests again
    })
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);
    }

    public UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(ImmutableList.of(new Rating("0", 0)));
        return userRating;
    }

}
