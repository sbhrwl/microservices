package org.example;

import jakarta.inject.Inject;

public class RegistrationRepository {

    private final DatabaseConnection connection;

    @Inject
    public RegistrationRepository(
            DatabaseConnection connection) {

        this.connection = connection;
    }

    public void save() {
        System.out.println("Saving meter registration...");
    }
}