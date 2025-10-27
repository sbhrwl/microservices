package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiFutureCallback;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.cloud.pubsub.v1.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Profile("prod")
public class MessagePublisherPubSub {

    private final Publisher requestPublisher;
    private final Publisher responsePublisher;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MessagePublisherPubSub(Publisher requestPublisher, Publisher responsePublisher) {
        this.requestPublisher = requestPublisher;
        this.responsePublisher = responsePublisher;
    }

    private void publish(Publisher publisher, String msgType, MessagePayload payload) {
        try {
            ByteString data = ByteString.copyFromUtf8(payload.toString());
            PubsubMessage message = PubsubMessage.newBuilder().setData(data).build();

            ApiFuture<String> future = publisher.publish(message);
            ApiFutures.addCallback(future, new ApiFutureCallback<>() {
                @Override
                public void onFailure(Throwable t) {
                    System.err.println("Failed to publish " + msgType + ": " + t.getMessage());
                }

                @Override
                public void onSuccess(String messageId) {
                    System.out.println("Published " + msgType + " successfully. Message ID: " + messageId);
                }
            }, executor);
        } catch (Exception e) {
            System.err.println("Error preparing message for " + msgType + ": " + e.getMessage());
        }
    }

    public void publishToRequestQueue(MessagePayload payload) {
        publish(requestPublisher, "request", payload);
    }

    public void publishToResponseQueue(MessagePayload payload) {
        publish(responsePublisher, "response", payload);
    }
}
