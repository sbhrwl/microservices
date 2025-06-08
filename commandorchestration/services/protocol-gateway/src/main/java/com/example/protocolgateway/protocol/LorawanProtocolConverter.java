package com.example.protocolgateway.protocol;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("lorawan")
public class LorawanProtocolConverter implements ProtocolConverter {

    private static final Logger logger = LoggerFactory.getLogger(LorawanProtocolConverter.class);

    @Override
    public byte[] convertCommand(CommandMessage commandMessage) {
        logger.info("Converting command with ID {} for LoRaWAN protocol", commandMessage.getId());

        String lorawanCommand = "LORAWAN:" + commandMessage.getId() + ":" + commandMessage.getSensorId();

        logger.debug("Converted LoRaWAN command string: {}", lorawanCommand);
        return lorawanCommand.getBytes();
    }
}
