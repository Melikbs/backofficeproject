import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GammeService {
  private apiUrl = 'http://localhost:8080/api/gammes'; // ton endpoint backend

  constructor(private http: HttpClient) {}

  getAllGammes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`).pipe(
      tap((data) => {
        console.log('Gammes retrieved:', data);  // Vérifie les données dans la console
      })
    );
  }
  

  createGamme(gamme: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create`, gamme);
  }

  updateGamme(gamme: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update/${gamme.codeGamme}`, gamme);
  }
  

  toggleFlag(codeGamme: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/flag/${codeGamme}`, {});
  }
  

  deleteGamme(codeGamme: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${codeGamme}`);
  }
}
