package com.example.protocolgateway.service;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;
import com.example.protocolgateway.protocol.ProtocolConverter;
import com.example.protocolgateway.protocol.ProtocolConverterFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CommandMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(CommandMessageListener.class);

    private final ProtocolConverterFactory protocolConverterFactory;

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

            ProtocolConverter converter = protocolConverterFactory.getConverter(); // no argument now

            byte[] convertedCommand = converter.convertCommand(commandMessage);
            logger.info("Converted command bytes: {}", new String(convertedCommand));

            // TODO: Send convertedCommand to the sensor or downstream system

        } catch (Exception e) {
            logger.error("Failed to process CommandMessage", e);
        }
    }
}
