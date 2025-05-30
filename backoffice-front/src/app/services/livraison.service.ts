import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Livraison } from '../models/livraison.model';

@Injectable({
  providedIn: 'root'
})
export class LivraisonService {
  private apiUrl = 'http://localhost:8080/api/livraisons';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(this.apiUrl);
  }

  validerLivraison(id: number): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.apiUrl}/${id}/valider`, {});
  }
  
  getById(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.apiUrl}/${id}`);
  }
  
  startTracking(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/${id}/start-tracking`, {}, { responseType: 'text' });
  }
  
}
