package org.example;

import dagger.Component;

@Component
public interface AppComponent {

    MeterRegistrationService meterRegistrationService();
}