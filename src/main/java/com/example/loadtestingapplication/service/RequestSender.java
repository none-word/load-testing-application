package com.example.loadtestingapplication.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@AllArgsConstructor
@Service
public class RequestSender {
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private static final Map<String, Integer> statistics;

    static {
        statistics = new ConcurrentHashMap<>();
        statistics.put("TV 1", 0);
        statistics.put("TV 2", 0);
        statistics.put("TV 3", 0);
    }

    @PostConstruct
    public void sendRequests() throws InterruptedException {
        Runnable task = () -> {
            String responseString = getResponse();
            updateStatistics(responseString);
        };
        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }
        Thread.sleep(10000);
        log.info("Statistics: {}", statistics);
    }

    private static void updateStatistics(String responseString) {
        responseString = responseString.substring(responseString.indexOf("<title>") + 7);
        responseString = responseString.substring(0, responseString.indexOf("</title>"));
        statistics.compute(responseString, (k, v) -> v + 1);
    }

    private String getResponse() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://51.250.88.200", String.class);
        return responseEntity.getBody();
    }
}
