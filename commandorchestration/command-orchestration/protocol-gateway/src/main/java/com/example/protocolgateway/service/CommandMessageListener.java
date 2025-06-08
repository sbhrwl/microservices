package com.example.protocolgateway.service;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;
import com.example.protocolgateway.protocol.ProtocolConverter;
import com.example.protocolgateway.protocol.ProtocolConverterFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommandMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(CommandMessageListener.class);

    private final ProtocolConverterFactory protocolConverterFactory;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sensor.simulator.url}")
    private String sensorSimulatorUrl;

    public CommandMessageListener(ProtocolConverterFactory protocolConverterFactory) {
        this.protocolConverterFactory = protocolConverterFactory;
    }

    @KafkaListener(
        topics = "${kafka.topic.command-dispatch}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(ConsumerRecord<String, byte[]> record) {
        try {
            CommandMessage commandMessage = CommandMessage.parseFrom(record.value());

            logger.info("Received CommandMessage: ID={}, SensorID={}, Type={}, Args={}",
                    commandMessage.getId(),
                    commandMessage.getSensorId(),
                    commandMessage.getCommandType(),
                    commandMessage.getCommandArgsList());

            ProtocolConverter converter = protocolConverterFactory.getConverter();
            byte[] convertedCommand = converter.convertCommand(commandMessage);

            logger.info("Converted command bytes: {}", new String(convertedCommand));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(convertedCommand, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(sensorSimulatorUrl, requestEntity, String.class);
            logger.info("Sensor simulator response: {}", response.getBody());

        } catch (Exception e) {
            logger.error("Failed to process CommandMessage", e);
        }
    }
}
