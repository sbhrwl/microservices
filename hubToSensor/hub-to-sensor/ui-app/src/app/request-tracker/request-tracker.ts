import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { Injectable } from '@angular/core';
import { PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { Observable, of } from 'rxjs';

// Define interfaces to match your DTOs for type safety
interface ControlRequestDTO {
  id: number;
  duration: number;
  operation: string;
  relayNumber: number;
  sensorId: string;
  status: string;
}

interface ChangeLogDTO {
  id: number;
  changeDescription: string;
  changeTimestamp: string;
}

@Injectable({ providedIn: 'root' })
export class RequestDataService {
  constructor(private http: HttpClient) {}

  getAllRequests(): Observable<ControlRequestDTO[]> {
    // Replace with actual API endpoint as needed
    return this.http
      ? this.http.get<ControlRequestDTO[]>('/api/v1/requests')
      : of([]);
  }

  getRequestLogs(id: number): Observable<ChangeLogDTO[]> {
    // Replace with actual API endpoint as needed
    return this.http
      ? this.http.get<ChangeLogDTO[]>(`/api/v1/requests/${id}/logs`)
      : of([]);
  }
}

@Component({
  selector: 'app-request-tracker',
  standalone: true,
  imports: [CommonModule, DatePipe, HttpClientModule], 
  template: `
    @if (error()) {
      <p class="error-message">{{ error() }}</p>
    } @else if (isLoading()) {
      <p class="loading">Loading requests...</p>
    } @else {
      <div class="tracker-container">
        
        <div class="request-list">
          <h2>All Requests ({{ allRequests().length }})</h2>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Sensor ID</th>
                <th>Operation</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              @for (request of allRequests(); track request.id) {
                <tr 
                  [class.selected]="request.id === selectedRequest()?.id"
                  (click)="selectRequest(request)"
                >
                  <td>{{ request.id }}</td>
                  <td>{{ request.sensorId }}</td>
                  <td>{{ request.operation }}</td>
                  <td>{{ request.status }}</td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <div class="log-details">
          @if (selectedRequest()) {
            <h2>Change Logs for ID: {{ selectedRequest()!.id }}</h2>
            @if (changeLogs().length === 0) {
              <p class="no-logs">No change logs found for this request.</p>
            } @else {
              <table>
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  @for (log of changeLogs(); track log.id) {
                    <tr>
                      <td>{{ log.changeTimestamp | date:'medium' }}</td> 
                      <td>{{ log.changeDescription }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          } @else {
            <p class="selection-prompt">Click a request on the left to view its history.</p>
          }
        </div>
      </div>
    }
  `,
  styles: [
    `
      .tracker-container {
        display: flex;
        gap: 20px;
      }

      .request-list, .log-details {
        flex: 1;
        padding: 15px;
        border: 1px solid #E0E0E0; /* Light border */
        border-radius: 6px;
        background-color: #FAFAFA; /* Very light gray background */
      }

      h2 {
        border-bottom: 1px solid #F0F0F0; /* Lighter divider */
        padding-bottom: 8px;
        color: #555; /* Medium gray text */
        margin-top: 0;
      }

      table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
        font-size: 0.9em;
      }

      th {
        background-color: #EFEFEF; /* Light shade for header */
        color: #333;
        padding: 10px 8px;
        text-align: left;
        border-bottom: 1px solid #D0D0D0;
      }

      td {
        padding: 8px;
        border-bottom: 1px solid #F7F7F7; /* Very light line for rows */
      }

      tr:hover {
        background-color: #F0F8FF; /* Light blue on hover for lists */
        cursor: pointer;
      }
      
      tr.selected {
        background-color: #E6F3FF; /* Light blue for selected row */
        font-weight: bold;
      }

      .selection-prompt, .no-logs {
        padding: 20px;
        text-align: center;
        color: #777;
        background-color: #FFFFFF;
        border-radius: 4px;
        border: 1px dashed #DDD;
      }
      
      .error-message {
        color: #C00;
        background-color: #FEE;
        padding: 10px;
        border: 1px solid #FAA;
        border-radius: 4px;
      }
      
      .loading {
        color: #007bff;
        padding: 10px;
      }
    `
  ]
})
export class RequestTrackerComponent implements OnInit {

  // Signals hold the component state
  allRequests = signal<ControlRequestDTO[]>([]);
  selectedRequest = signal<ControlRequestDTO | null>(null);
  changeLogs = signal<ChangeLogDTO[]>([]);
  isLoading = signal<boolean>(true);
  error = signal<string | null>(null);

  private dataService = inject(RequestDataService);

  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAllRequests();
    } else {
      this.isLoading.set(false);
    }
  }

  /**
   * Calls API 0: Fetches the initial list of all control requests.
   */
  loadAllRequests(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.dataService.getAllRequests().subscribe({
      next: (data: ControlRequestDTO[]) => {
        this.allRequests.set(data);
        this.isLoading.set(false);
      },
      error: (err: unknown) => {
        console.error('Error fetching requests:', err);
        this.error.set('Failed to load requests. Check if the server is running on port 8085.');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Handles request selection and loads the change logs for the detail view.
   * Uses API 2: GET /api/v1/requests/{id}/logs
   */
  selectRequest(request: ControlRequestDTO): void {
    this.selectedRequest.set(request);
    this.changeLogs.set([]); // Clear previous logs
    this.error.set(null);

    this.dataService.getRequestLogs(request.id).subscribe({
      next: (logs: ChangeLogDTO[]) => {
        this.changeLogs.set(logs);
      },
      error: (err: unknown) => {
        console.error('Error fetching logs:', err);
        this.error.set(`Failed to load logs for ID ${request.id}.`);
      }
    });
  }
}