import { Component, OnInit, inject, signal, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { RequestDataService, ControlRequestDTO, ChangeLogDTO } from './request-data.service';

@Component({
  selector: 'app-request-tracker',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './request-tracker.component.html',
  styleUrls: ['./request-tracker.component.css']
})
export class RequestTrackerComponent implements OnInit {
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

  loadAllRequests(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.dataService.getAllRequests().subscribe({
      next: (data) => {
        this.allRequests.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching requests:', err);
        this.error.set('Failed to load requests. Check if the server is running.');
        this.isLoading.set(false);
      }
    });
  }

  selectRequest(request: ControlRequestDTO): void {
    this.selectedRequest.set(request);
    this.changeLogs.set([]);
    this.error.set(null);

    this.dataService.getRequestLogs(request.id).subscribe({
      next: (logs) => {
        this.changeLogs.set(logs);
      },
      error: (err) => {
        console.error(`Error fetching logs for request ID ${request.id}:`, err);
        this.error.set(`Failed to load logs for Request ID ${request.id}.`);
      }
    });
  }

  // --- TrackBy functions ---
  trackByRequestId(index: number, item: ControlRequestDTO): number {
    return item.id;
  }

  trackByLogId(index: number, item: ChangeLogDTO): number {
    return item.id;
  }
}
