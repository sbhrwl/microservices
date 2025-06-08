package com.example.protocolgateway.protocol;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProtocolConverterFactory {

    private final Map<String, ProtocolConverter> converters;

    public ProtocolConverterFactory(Map<String, ProtocolConverter> converters) {
        this.converters = converters;
    }

    public ProtocolConverter getConverter(CommandMessage commandMessage) {
        String protocolKey = commandMessage.getCommandType().name().toLowerCase();

        ProtocolConverter converter = converters.get(protocolKey);
        if (converter == null) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocolKey);
        }
        return converter;
    }
}
