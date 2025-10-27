import { Component, signal } from '@angular/core';
import { RequestTrackerComponent } from './request-tracker/request-tracker';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RequestTrackerComponent], 
  template: `
    <main>
      <h1>Control Request Tracker</h1>
      <app-request-tracker></app-request-tracker>
    </main>
  `,
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('ui-app');
}