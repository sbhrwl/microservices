package com.example.protocolgateway.protocol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProtocolConverterFactory {

    private final Map<String, ProtocolConverter> converters;
    private final String protocol;

    public ProtocolConverterFactory(Map<String, ProtocolConverter> converters,
                                    @Value("${protocol.type:dlms}") String protocol) {
        this.converters = converters;
        this.protocol = protocol.toLowerCase();
    }

    public ProtocolConverter getConverter() {
        ProtocolConverter converter = converters.get(protocol);
        if (converter == null) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
        return converter;
    }
}
