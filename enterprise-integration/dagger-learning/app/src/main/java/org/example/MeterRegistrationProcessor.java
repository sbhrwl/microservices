package org.example;

import jakarta.inject.Inject;

public class MeterRegistrationProcessor {

    private final RegistrationRepository repository;
    private final Validator validator;

    @Inject
    public MeterRegistrationProcessor(
            RegistrationRepository repository,
            Validator validator) {

        this.repository = repository;
        this.validator = validator;
    }

    public void process() {
        validator.validate();
        repository.save();
    }
}