import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GraphQLService, Sensor } from './services/graphql.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Sensor Registration System';
  
  // Form models
  registerForm = {
    sensorId: '',
    userEmail: '',
    postcode: ''
  };

  searchSensorId = '';
  searchUserEmail = '';
  updateForm = {
    sensorId: '',
    postcode: ''
  };

  // Data
  currentSensor: Sensor | null = null;
  userSensors: Sensor[] = [];
  
  // UI state
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;
  activeTab: 'register' | 'search' | 'list' | 'update' = 'register';

  constructor(private graphqlService: GraphQLService) {}

  setActiveTab(tab: 'register' | 'search' | 'list' | 'update') {
    this.activeTab = tab;
    this.clearMessages();
  }

  clearMessages() {
    this.error = null;
    this.successMessage = null;
  }

  registerSensor() {
    if (!this.registerForm.sensorId || !this.registerForm.userEmail || !this.registerForm.postcode) {
      this.error = 'All fields are required';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.graphqlService.registerSensor(
      this.registerForm.sensorId,
      this.registerForm.userEmail,
      this.registerForm.postcode
    ).subscribe({
      next: (sensor) => {
        this.loading = false;
        this.successMessage = `Sensor ${sensor.sensorId} registered successfully!`;
        this.currentSensor = sensor;
        this.registerForm = { sensorId: '', userEmail: '', postcode: '' };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.message || 'Failed to register sensor';
      }
    });
  }

  searchSensor() {
    if (!this.searchSensorId) {
      this.error = 'Sensor ID is required';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.graphqlService.getSensor(this.searchSensorId).subscribe({
      next: (sensor) => {
        this.loading = false;
        this.currentSensor = sensor;
        this.successMessage = 'Sensor found!';
      },
      error: (err) => {
        this.loading = false;
        this.currentSensor = null;
        this.error = err.message || 'Sensor not found';
      }
    });
  }

  listSensorsByUser() {
    if (!this.searchUserEmail) {
      this.error = 'User email is required';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.graphqlService.listSensorsByUser(this.searchUserEmail).subscribe({
      next: (sensors) => {
        this.loading = false;
        this.userSensors = sensors;
        this.successMessage = `Found ${sensors.length} sensor(s)`;
      },
      error: (err) => {
        this.loading = false;
        this.userSensors = [];
        this.error = err.message || 'Failed to fetch sensors';
      }
    });
  }

  updateSensor() {
    if (!this.updateForm.sensorId || !this.updateForm.postcode) {
      this.error = 'All fields are required';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.graphqlService.updateSensorPostcode(
      this.updateForm.sensorId,
      this.updateForm.postcode
    ).subscribe({
      next: (sensor) => {
        this.loading = false;
        this.successMessage = `Sensor ${sensor.sensorId} updated successfully!`;
        this.currentSensor = sensor;
        this.updateForm = { sensorId: '', postcode: '' };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.message || 'Failed to update sensor';
      }
    });
  }
}