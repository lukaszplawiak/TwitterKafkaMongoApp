package com.lukaszplawiak.twitterkafkamongoapp.repository;

import com.lukaszplawiak.twitterkafkamongoapp.entity.TwitterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TwitterRepository extends MongoRepository<TwitterEntity, String> {

    Page<TwitterEntity> findAll(Pageable pageable);
    List<TwitterEntity> findAllByAuthorId(String authorId);
    void deleteTwitterEntitiesByAuthorId(String authorId);
}
