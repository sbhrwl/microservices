package com.apexsphere.flexibility_hub_simulator.config;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("prod") // only active in production
public class PubSubConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${messaging.pubsub.request-topic}")
    private String requestTopic;

    @Value("${messaging.pubsub.response-topic}")
    private String responseTopic;

    @Value("${messaging.pubsub.request-subscription}")
    private String requestSubscription;

    @Value("${messaging.pubsub.response-subscription}")
    private String responseSubscription;

    @Bean(destroyMethod = "shutdown")
    public Publisher requestPublisher() throws IOException {
        ProjectTopicName topicName = ProjectTopicName.of(projectId, requestTopic);
        return Publisher.newBuilder(topicName).build();
    }

    @Bean(destroyMethod = "shutdown")
    public Publisher responsePublisher() throws IOException {
        ProjectTopicName topicName = ProjectTopicName.of(projectId, responseTopic);
        return Publisher.newBuilder(topicName).build();
    }

    // ✅ getters for consumer usage
    public String getProjectId() {
        return projectId;
    }

    public String getRequestSubscription() {
        return requestSubscription;
    }

    public String getResponseSubscription() {
        return responseSubscription;
    }

    public String getResponseTopic() {
        return responseTopic;
    }

    public String getRequestTopic() {
        return requestTopic;
    }
}
