import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Marque } from '../models/marque.model'; // importe ton vrai modèle

@Injectable({
  providedIn: 'root',
})
export class MarqueService {
  private apiUrl = 'http://localhost:8080/api/marques';

  constructor(private http: HttpClient) {}

  getAllMarques(): Observable<Marque[]> {
    return this.http.get<Marque[]>(this.apiUrl);
  }

  createMarque(marque: Marque): Observable<Marque> {
    return this.http.post<Marque>(`${this.apiUrl}/create`, marque);
  }

  deleteMarque(codeMarque: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codeMarque}`);
  }
  updateMarque(marque: Marque): Observable<Marque> {
    return this.http.put<Marque>(`${this.apiUrl}/${marque.codeMarque}`, marque);
  }
  

  toggleActif(codeMarque: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${codeMarque}/toggle-flag`, {}); 
  }
}
