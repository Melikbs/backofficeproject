import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Pack } from '../models/pack.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PackService {
  private apiUrl = 'http://localhost:8080/api/packs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Pack[]> {
    return this.http.get<Pack[]>(this.apiUrl);
  }

  create(pack: Pack): Observable<Pack> {
    // Correction ici (retirer /create)
    return this.http.post<Pack>(this.apiUrl, pack);
  }
  update(pack: Pack): Observable<Pack> {
    return this.http.put<Pack>(`${this.apiUrl}/${pack.id}`, pack);
  }
  
  
  toggleActif(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/disable`, {}); // fonctionne pour activer/désactiver
  }
  
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  
}
