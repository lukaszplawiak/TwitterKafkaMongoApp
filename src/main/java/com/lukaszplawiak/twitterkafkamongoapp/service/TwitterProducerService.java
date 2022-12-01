package com.lukaszplawiak.twitterkafkamongoapp.service;

import com.lukaszplawiak.twitterkafkamongoapp.dto.PayloadDto;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.dto.tweet.TweetV2;
import io.github.redouane59.twitter.helpers.ConverterHelper;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TwitterProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterProducerService.class);
    private static final String TOPIC = "twitter_test_1";
    private final KafkaTemplate<String, PayloadDto> kafkaTemplate;
    private final TwitterClient twitterClient;

    public TwitterProducerService(KafkaTemplate<String, PayloadDto> kafkaTemplate) {
        String bearerToken = System.getenv("TWITTER_BEARER_TOKEN");
        twitterClient = new TwitterClient(TwitterCredentials.builder()
                .bearerToken(bearerToken)
                .build());
        this.kafkaTemplate = kafkaTemplate;
    }

    public void runProducer(List<PayloadDto> tweetDataList) {
        tweetDataList.forEach(tweet -> {
            LOGGER.info("PRODUCER | createdAt: '{}' | authorId: '{}' | authorName: '{}' | text: '{}'",
                    tweet.getCreatedAt(), tweet.getAuthorId(), tweet.getAuthorName(), tweet.getTweetText());
            try {
                kafkaTemplate.send(new ProducerRecord<>(TOPIC, tweet)).get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Producer cannot send Producer Record", e);
            }
        });
    }

    public List<PayloadDto> getPayload(String keyword, int minutes) {
        int endTime = 61; // twitter api min daley
        int startTime = endTime + minutes;

        AdditionalParameters parameters = AdditionalParameters.builder()
                .startTime(ConverterHelper.minutesBeforeNow(startTime))
                .endTime(ConverterHelper.minutesBeforeNow(endTime))
                .recursiveCall(true)
                .build();

        TweetList result = twitterClient.searchTweets(keyword, parameters);
        return mapToPayloadDtoList(result.getData());
    }

    private List<PayloadDto> mapToPayloadDtoList(List<TweetV2.TweetData> tweetDataList) {
        return tweetDataList.stream()
                .map(tweet -> new PayloadDto(
                        tweet.getAuthorId(),
                        tweet.getUser().getName(),
                        tweet.getCreatedAt().toString(),
                        tweet.getText()
                )).collect(Collectors.toList());
    }
}

