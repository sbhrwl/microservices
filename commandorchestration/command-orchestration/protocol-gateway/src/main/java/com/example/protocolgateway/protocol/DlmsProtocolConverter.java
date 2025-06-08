package com.example.protocolgateway.protocol;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("dlms")
public class DlmsProtocolConverter implements ProtocolConverter {

    private static final Logger logger = LoggerFactory.getLogger(DlmsProtocolConverter.class);

    @Override
    public byte[] convertCommand(CommandMessage commandMessage) {
        logger.info("Converting command with ID {} for DLMS protocol", commandMessage.getId());

        // TODO: implement actual DLMS protocol conversion logic here
        byte[] result = commandMessage.getId().getBytes();

        logger.debug("Converted DLMS command bytes: {}", new String(result));
        return result;
    }
}
