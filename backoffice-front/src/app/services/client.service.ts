import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({ providedIn: 'root' })
export class ClientService {
  private apiUrl = 'http://localhost:8080/api/clients';

  constructor(private http: HttpClient) {}

  getClients(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  deactivateClient(codeClient: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${codeClient}/deactivate`, {});
  }

  getBlacklistedClients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/blacklisted`);
  }
  reactivateClient(codeClient: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${codeClient}/reactivate`, null);
  }
  
  
}
