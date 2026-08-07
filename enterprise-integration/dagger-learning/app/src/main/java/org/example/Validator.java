package org.example;

import jakarta.inject.Inject;

public class Validator {

    @Inject
    public Validator() {
    }

    public void validate() {
        System.out.println("Validation successful");
    }
}