package org.example;

public class App {

    public static void main(String[] args) {

        AppComponent component =
                DaggerAppComponent.create();

        MeterRegistrationService service =
                component.meterRegistrationService();

        service.registerMeter();
    }
}