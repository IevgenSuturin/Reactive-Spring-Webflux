package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.model.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);
    Flux<MovieInfo> findByYearAndName(Integer year, String name);
}
