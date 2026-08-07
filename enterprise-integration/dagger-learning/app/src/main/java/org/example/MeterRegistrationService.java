package org.example;

import jakarta.inject.Inject;

public class MeterRegistrationService {

    private final MeterRegistrationProcessor processor;

    @Inject
    public MeterRegistrationService(
            MeterRegistrationProcessor processor) {

        this.processor = processor;
    }

    public void registerMeter() {
        processor.process();
    }
}