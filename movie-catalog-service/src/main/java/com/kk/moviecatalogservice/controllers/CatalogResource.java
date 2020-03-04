package com.kk.moviecatalogservice.controllers;

import com.kk.moviecatalogservice.hystrixwrapperproxyservices.MovieInfo;
import com.kk.moviecatalogservice.hystrixwrapperproxyservices.UserRatingInfo;
import com.kk.moviecatalogservice.models.CatalogItem;
import com.kk.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder; // Spring flux

    @Autowired
    UserRatingInfo userRatingInfo;

    @Autowired
    MovieInfo movieInfo;

//    @Autowired
//    private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        UserRating userRating = userRatingInfo.getUserRating(userId);
        return userRating.getRatings().stream().map(rating -> movieInfo.getCatalogItem(rating)).collect(Collectors.toList());
    }
}
