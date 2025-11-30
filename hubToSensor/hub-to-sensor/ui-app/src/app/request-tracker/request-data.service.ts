import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

export interface ControlRequestDTO {
  id: number;
  duration: number;
  operation: string;
  relayNumber: number;
  sensorId: string;
  status: string;
}

export interface ChangeLogDTO {
  id: number;
  description: string;       // ✅ match backend field
  changeTimestamp: string;
}

@Injectable({ providedIn: 'root' })
export class RequestDataService {
  constructor(private http: HttpClient) {}

  getAllRequests(): Observable<ControlRequestDTO[]> {
    return this.http.get<ControlRequestDTO[]>(`${environment.apiBaseUrl}/v1/requests`);
  }

  getRequestLogs(id: number): Observable<ChangeLogDTO[]> {
    return this.http.get<ChangeLogDTO[]>(`${environment.apiBaseUrl}/v1/requests/${id}/logs`);
  }
}
