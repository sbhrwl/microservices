import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Sensor {
  sensorId: string;
  userEmail: string;
  postcode: string;
  status: string;
  registeredAt: string;
}

interface GraphQLResponse<T> {
  data: T;
  errors?: Array<{ message: string }>;
}

@Injectable({
  providedIn: 'root'
})
export class GraphQLService {
  private readonly graphqlUrl = 'http://localhost:4000/graphql';

  constructor(private http: HttpClient) {}

  registerSensor(sensorId: string, userEmail: string, postcode: string): Observable<Sensor> {
    const mutation = `
      mutation RegisterSensor($sensorId: String!, $userEmail: String!, $postcode: String!) {
        registerSensor(sensorId: $sensorId, userEmail: $userEmail, postcode: $postcode) {
          sensorId
          userEmail
          postcode
          status
          registeredAt
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ registerSensor: Sensor }>>(this.graphqlUrl, {
      query: mutation,
      variables: { sensorId, userEmail, postcode }
    }).pipe(
      map(response => {
        if (response.errors) {
          throw new Error(response.errors[0].message);
        }
        return response.data.registerSensor;
      })
    );
  }

  getSensor(sensorId: string): Observable<Sensor> {
    const query = `
      query GetSensor($sensorId: String!) {
        sensor(sensorId: $sensorId) {
          sensorId
          userEmail
          postcode
          status
          registeredAt
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ sensor: Sensor }>>(this.graphqlUrl, {
      query,
      variables: { sensorId }
    }).pipe(
      map(response => {
        if (response.errors) {
          throw new Error(response.errors[0].message);
        }
        return response.data.sensor;
      })
    );
  }

  listSensorsByUser(userEmail: string): Observable<Sensor[]> {
    const query = `
      query ListSensorsByUser($userEmail: String!) {
        sensorsByUser(userEmail: $userEmail) {
          sensorId
          userEmail
          postcode
          status
          registeredAt
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ sensorsByUser: Sensor[] }>>(this.graphqlUrl, {
      query,
      variables: { userEmail }
    }).pipe(
      map(response => {
        if (response.errors) {
          throw new Error(response.errors[0].message);
        }
        return response.data.sensorsByUser;
      })
    );
  }

  updateSensorPostcode(sensorId: string, postcode: string): Observable<Sensor> {
    const mutation = `
      mutation UpdateSensorPostcode($sensorId: String!, $postcode: String!) {
        updateSensorPostcode(sensorId: $sensorId, postcode: $postcode) {
          sensorId
          userEmail
          postcode
          status
          registeredAt
        }
      }
    `;

    return this.http.post<GraphQLResponse<{ updateSensorPostcode: Sensor }>>(this.graphqlUrl, {
      query: mutation,
      variables: { sensorId, postcode }
    }).pipe(
      map(response => {
        if (response.errors) {
          throw new Error(response.errors[0].message);
        }
        return response.data.updateSensorPostcode;
      })
    );
  }
}