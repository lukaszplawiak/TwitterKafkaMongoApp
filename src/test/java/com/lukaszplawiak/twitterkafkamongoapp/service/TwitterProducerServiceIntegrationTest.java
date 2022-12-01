package com.lukaszplawiak.twitterkafkamongoapp.service;

import com.lukaszplawiak.twitterkafkamongoapp.dto.PayloadDto;
import com.lukaszplawiak.twitterkafkamongoapp.repository.TwitterRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = {"twitter_test_1"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TwitterProducerServiceIntegrationTest {
    private static final String TOPIC_EXAMPLE = "twitter_test_1";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private TwitterProducerService twitterProducerService;

    @Autowired
    private TwitterRepository twitterRepository;

    @Test
    public void should_ProduceCorrectPayloadDtoTweet_To_Topic_twitter_test_1() {
        // given
        PayloadDto payloadDto = new PayloadDto(
                "1234", "TestName", "2022-12-12T12:12:12.000Z", "ProducerTextTest");
        List<PayloadDto> payloadDtoList = new ArrayList<>();
        payloadDtoList.add(payloadDto);

        // simulation consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "myGroup", "false", embeddedKafkaBroker);
        consumerProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ConsumerFactory<String, PayloadDto> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), new JsonDeserializer<>(PayloadDto.class, false));
        Consumer<String, PayloadDto> consumerServiceTest = consumerFactory.createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerServiceTest, TOPIC_EXAMPLE);

        // when
        twitterProducerService.runProducer(payloadDtoList);

        // then
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            ConsumerRecord<String, PayloadDto> consumerRecordOfExampleTwit = KafkaTestUtils.getSingleRecord(
                    consumerServiceTest, TOPIC_EXAMPLE);

            PayloadDto valueReceived = consumerRecordOfExampleTwit.value();

            assertEquals(payloadDto.getCreatedAt(), valueReceived.getCreatedAt());
            assertEquals(payloadDto.getAuthorId(), valueReceived.getAuthorId());
            assertEquals(payloadDto.getAuthorName(), valueReceived.getAuthorName());
            assertEquals(payloadDto.getTweetText(), valueReceived.getTweetText());
        });
        twitterRepository.deleteTwitterEntitiesByAuthorId(payloadDto.getAuthorId());
        consumerServiceTest.close();
    }
}