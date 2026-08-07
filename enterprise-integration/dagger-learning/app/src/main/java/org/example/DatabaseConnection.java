package org.example;

import jakarta.inject.Inject;

public class DatabaseConnection {

    @Inject
    public DatabaseConnection() {
        System.out.println("Database connection created");
    }
}