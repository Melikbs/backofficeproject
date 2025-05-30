import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Couleur } from '../models/couleur.model';  // Crée un modèle de données pour "Couleur"

@Injectable({
  providedIn: 'root'
})
export class CouleurService {

  private apiUrl = 'http://localhost:8080/api/couleurs'; // Remplace par l'URL correcte de ton API

  constructor(private http: HttpClient) {}

  // Récupère toutes les couleurs
  getCouleurs(): Observable<Couleur[]> {
    return this.http.get<Couleur[]>(this.apiUrl);
  }
}
