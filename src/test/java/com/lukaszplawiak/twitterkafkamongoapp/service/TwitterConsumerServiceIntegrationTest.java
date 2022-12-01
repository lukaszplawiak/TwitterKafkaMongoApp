package com.lukaszplawiak.twitterkafkamongoapp.service;

import com.lukaszplawiak.twitterkafkamongoapp.dto.PayloadDto;
import com.lukaszplawiak.twitterkafkamongoapp.entity.TwitterEntity;
import com.lukaszplawiak.twitterkafkamongoapp.repository.TwitterRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = {"twitter_test_1"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TwitterConsumerServiceIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerServiceIntegrationTest.class);
    private static final String TOPIC_EXAMPLE = "twitter_test_1";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private TwitterRepository twitterRepository;

    @Test
    public void should_ConsumeCorrectPayloadDtoTweet_from_TOPIC_twitter_test_1_and_should_saveCorrectExampleEntity() {
        // given
        PayloadDto payload = new PayloadDto(
                "1234", "TestName", "2022-12-12T12:12:12.000Z", "ConsumerTextTest");

        // simulation consumer
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker.getBrokersAsString());
        LOGGER.info("props {}", producerProps);

        KafkaProducer<String, PayloadDto> producerTest = new KafkaProducer<>(
                producerProps, new StringSerializer(), new JsonSerializer<>());

        // when
        producerTest.send(new ProducerRecord<>(TOPIC_EXAMPLE, "", payload));

        // then
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var exampleEntityList = twitterRepository.findAllByAuthorId("1234");
            assertEquals(1, exampleEntityList.size());
            TwitterEntity firstEntity = exampleEntityList.get(0);
            assertEquals(payload.getCreatedAt(), firstEntity.getCreatedAt());
            assertEquals(payload.getAuthorId(), firstEntity.getAuthorId());
            assertEquals(payload.getAuthorName(), firstEntity.getAuthorName());
            assertEquals(payload.getTweetText(), firstEntity.getTweetText());
        });
        twitterRepository.deleteTwitterEntitiesByAuthorId(payload.getAuthorId());
        producerTest.close();
    }
}