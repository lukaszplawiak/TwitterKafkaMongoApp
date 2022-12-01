package com.lukaszplawiak.twitterkafkamongoapp.controller;

import com.lukaszplawiak.twitterkafkamongoapp.dto.PayloadDto;
import com.lukaszplawiak.twitterkafkamongoapp.dto.RequestDto;
import com.lukaszplawiak.twitterkafkamongoapp.entity.TwitterEntity;
import com.lukaszplawiak.twitterkafkamongoapp.repository.TwitterRepository;
import com.lukaszplawiak.twitterkafkamongoapp.service.TwitterProducerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class TwitterController {
    private final TwitterProducerService twitterProducerService;
    private final TwitterRepository twitterRepository;

    public TwitterController(TwitterProducerService twitterProducerService,
                             TwitterRepository twitterRepository) {
        this.twitterProducerService = twitterProducerService;
        this.twitterRepository = twitterRepository;
    }

    @ApiIgnore
    @RequestMapping(path = "/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @PostMapping(path = "/producer/run")
    ResponseEntity<String> producerRun(@RequestBody RequestDto requestDto) {
        List<PayloadDto> payloadDtoList = twitterProducerService.getPayload(requestDto.getKeyword(), requestDto.getMinute());
        twitterProducerService.runProducer(payloadDtoList);
        return new ResponseEntity<>("Producer started", HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/data/findAll")
    ResponseEntity<Page<TwitterEntity>> findAll(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(twitterRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(path = "/data/findByAuthorId/{id}")
    ResponseEntity<List<TwitterEntity>> findByAuthorId(@PathVariable String id) {
        return new ResponseEntity<>(twitterRepository.findAllByAuthorId(id), HttpStatus.OK);
    }

    @DeleteMapping(path = "/data/deleteTweetsByAuthorId/{id}")
    ResponseEntity<?> deleteTweetsByAuthorId(@PathVariable String id) {
        twitterRepository.deleteTwitterEntitiesByAuthorId(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}


