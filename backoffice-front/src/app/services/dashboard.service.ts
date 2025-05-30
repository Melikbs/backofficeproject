import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardStatistics {
  salesToday: number;
  salesThisWeek: number;
  salesThisMonth: number;

  revenueToday: number;
  revenueThisWeek: number;
  revenueThisMonth: number;

  topProducts: Record<string, number>;
  pendingOrders: number;
  topClients: Record<string, number>;
  aiSummary: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getStatistics(): Observable<DashboardStatistics> {
    return this.http.get<DashboardStatistics>(`${this.apiUrl}/statistics`);
  }

  sendChatMessage(message: string): Observable<{ response: string }> {
    return this.http.post<{ response: string }>(
      'http://localhost:8080/api/chatbot/message',
      { message }
    );
  }
  getRecommendationsForClient(clientId: number) {
  return this.http.get<any>(`http://localhost:8080/api/ia/recommendations/${clientId}`);
}
getAcheteurClients(): Observable<{ codeClient: number, nom: string, prenom: string }[]> {
  return this.http.get<any[]>('http://localhost:8080/api/clients/acheteurs');
}

}
