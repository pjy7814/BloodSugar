package kr.co.sugarmanager.alarmchallenge.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.RemindChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.service.AlarmChallengeService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmChallengeController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AlarmChallengeService alarmChallengeService;

    @Value(value = "${TOPIC}")
    private String TOPIC;
    @Scheduled(cron = "0 9 * * * *")
    @Scheduled(cron = "0 19 * * * *")
    @Scheduled(cron = "0 29 * * * *")
    @Scheduled(cron = "0 39 * * * *")
    @Scheduled(cron = "0 49 * * * *")
    @Scheduled(cron = "0 59 * * * *")
    @GetMapping("/challenge")
    public ResponseEntity<AlarmChallengeDTO.Response> getChallanges() throws JsonProcessingException {
        AlarmChallengeDTO.Response response = alarmChallengeService.getChallanges();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(TOPIC,stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Scheduled(cron = " 0 0 20 * * *")
    @GetMapping("/challenge/remind")
    public ResponseEntity<RemindChallengeDTO.Response> remind() throws JsonProcessingException {
        RemindChallengeDTO.Response response = alarmChallengeService.remind();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(TOPIC,stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
