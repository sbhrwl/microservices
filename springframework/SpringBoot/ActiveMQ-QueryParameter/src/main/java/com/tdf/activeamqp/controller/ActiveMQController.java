package com.tdf.activeamqp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tdf.activeamqp.model.CustomMessage;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/publish")
public class ActiveMQController {

    private final ActiveMQProducer activeMQProducer;

    @PostMapping
    public String publish(
        @RequestParam  String message) {
        String messageId = UUID.randomUUID().toString();
        log.info("Sending Message with Message Id : {}", messageId);
        activeMQProducer.produceMessage(UUID.randomUUID().toString(),
                CustomMessage.builder()
                        .id(messageId)
                        .message(message)
                        .build(),
                10
                );
        return "Done";
    }
}

