package com.lukaszplawiak.twitterkafkamongoapp.service;

import com.google.gson.Gson;
import com.lukaszplawiak.twitterkafkamongoapp.entity.TwitterEntity;
import com.lukaszplawiak.twitterkafkamongoapp.repository.TwitterRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TwitterConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerService.class);
    private static final String TOPIC = "twitter_test_1";
    private final TwitterRepository twitterRepository;

    public TwitterConsumerService(TwitterRepository twitterRepository) {
        this.twitterRepository = twitterRepository;
    }

    @KafkaListener(topics = TOPIC, groupId = "myGroup")
    public void consumeExampleDTO(ConsumerRecord<String, String> record) {
        LOGGER.info("CONSUMER | Key : '{}' | Partition: '{}' | Offset : '{}' | Topic: '{}' | Value : '{}'",
                record.key(), record.partition(), record.offset(), record.topic(), record.value());
        twitterRepository.save(parseToTwitterEntity(record.value()));
    }

    private TwitterEntity parseToTwitterEntity(String record) {
        Gson g = new Gson();
        return g.fromJson(record, TwitterEntity.class);
    }
}

