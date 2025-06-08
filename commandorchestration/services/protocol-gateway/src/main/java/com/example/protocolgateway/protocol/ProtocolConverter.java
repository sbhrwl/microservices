package com.example.protocolgateway.protocol;

import com.example.commandorchestrator.CommandMessageProto.CommandMessage;

public interface ProtocolConverter {
    /**
     * Converts a generic CommandMessage to a protocol-specific command payload (e.g., bytes or string).
     * @param commandMessage the generic protobuf command message
     * @return protocol-specific command as byte array
     */
    byte[] convertCommand(CommandMessage commandMessage);
}
